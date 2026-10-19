package me.whereareiam.socialismus.module.redis.common.network;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.network.api.transport.ReplicationTransport;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Carries the network's messages over Redis pub/sub. Each channel is received on a thread of its own, which
 * subscribes again whenever Redis drops the connection and tells the network once it is back.
 */
public final class RedisReplicationTransport implements ReplicationTransport, AutoCloseable {
	private static final Duration RETRY = Duration.ofSeconds(2);

	private final JedisPool pool;
	private final List<Subscription> subscriptions = new CopyOnWriteArrayList<>();
	private final List<Runnable> reconnectListeners = new CopyOnWriteArrayList<>();
	private volatile boolean connected = true;
	private volatile boolean closed;

	public RedisReplicationTransport(@NotNull JedisPool pool) {
		this.pool = pool;
	}

	@Override
	public @NotNull String name() {
		return "Redis";
	}

	@Override
	public void publish(@NotNull String channel, byte @NotNull [] message) {
		try (Jedis jedis = pool.getResource()) {
			jedis.publish(channel.getBytes(StandardCharsets.UTF_8), message);
		}
	}

	@Override
	public void subscribe(@NotNull String channel, @NotNull Consumer<byte[]> receiver) {
		Subscription subscription = new Subscription(channel, receiver);
		subscriptions.add(subscription);

		Thread thread = new Thread(subscription::run, "SocialismusRedis " + channel);
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public boolean isConnected() {
		return connected && !closed;
	}

	@Override
	public void onReconnect(@NotNull Runnable listener) {
		reconnectListeners.add(listener);
	}

	@Override
	public void close() {
		closed = true;
		subscriptions.forEach(Subscription::stop);
	}

	/**
	 * One channel's subscription, held by its thread for as long as the transport is open.
	 */
	private final class Subscription extends BinaryJedisPubSub {
		private final String channel;
		private final Consumer<byte[]> receiver;
		private volatile boolean lost;

		private Subscription(String channel, Consumer<byte[]> receiver) {
			this.channel = channel;
			this.receiver = receiver;
		}

		private void run() {
			while (!closed) {
				try (Jedis jedis = pool.getResource()) {
					jedis.subscribe(this, channel.getBytes(StandardCharsets.UTF_8));
				} catch (RuntimeException failure) {
					if (closed) return;
					if (!lost) Logger.warn("Lost Redis while receiving %s; retrying: %s", channel, failure.getMessage());

					lost = true;
					connected = false;
				}

				pause();
			}
		}

		@Override
		public void onSubscribe(byte[] subscribed, int count) {
			if (!lost) return;

			lost = false;
			connected = true;
			Logger.info("Redis is back for %s", channel);
			reconnectListeners.forEach(Runnable::run);
		}

		@Override
		public void onMessage(byte[] from, byte[] message) {
			try {
				receiver.accept(message);
			} catch (RuntimeException failure) {
				Logger.warn("A receiver of %s failed: %s", channel, failure);
			}
		}

		private void stop() {
			try {
				if (isSubscribed()) unsubscribe();
			} catch (RuntimeException ignored) {
				// The connection is gone already.
			}
		}

		private void pause() {
			try {
				Thread.sleep(RETRY.toMillis());
			} catch (InterruptedException interrupted) {
				Thread.currentThread().interrupt();
				closed = true;
			}
		}
	}
}
