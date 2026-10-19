package me.whereareiam.socialismus.module.network.testing;

import me.whereareiam.socialismus.module.network.api.transport.ReplicationTransport;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * A network in memory: the transports it hands out deliver to each other right away, on the caller's thread. A test
 * can take one off the network and bring it back, to see what a node misses and how it catches up.
 */
public final class InMemoryHub {
	private final List<HubTransport> transports = new CopyOnWriteArrayList<>();
	private final int maxMessageSize;

	/**
	 * Creates a network without a message size limit.
	 */
	public InMemoryHub() {
		this(Integer.MAX_VALUE);
	}

	/**
	 * Creates a network whose transports carry messages up to the given size, as a plugin message transport does.
	 */
	public InMemoryHub(int maxMessageSize) {
		this.maxMessageSize = maxMessageSize;
	}

	/**
	 * Returns a new transport on this network, connected.
	 */
	public @NotNull HubTransport transport(@NotNull String name) {
		HubTransport transport = new HubTransport(name);
		transports.add(transport);

		return transport;
	}

	private void deliver(HubTransport sender, String channel, byte[] message) {
		if (!sender.connected) return;
		if (message.length > maxMessageSize)
			throw new IllegalArgumentException(message.length + " bytes is more than the " + maxMessageSize + " this network carries");

		for (HubTransport transport : transports) {
			if (transport == sender || !transport.connected) continue;

			transport.receive(channel, message);
		}
	}

	/**
	 * A transport of an {@link InMemoryHub}.
	 */
	public final class HubTransport implements ReplicationTransport {
		private final String name;
		private final Map<String, List<Consumer<byte[]>>> receivers = new ConcurrentHashMap<>();
		private final List<Runnable> reconnectListeners = new CopyOnWriteArrayList<>();
		private volatile boolean connected = true;

		private HubTransport(String name) {
			this.name = name;
		}

		/**
		 * Takes this transport off the network: it neither sends nor receives until it reconnects.
		 */
		public void disconnect() {
			connected = false;
		}

		/**
		 * Brings this transport back and tells its listeners, as a real transport does after an outage.
		 */
		public void reconnect() {
			connected = true;
			reconnectListeners.forEach(Runnable::run);
		}

		private void receive(String channel, byte[] message) {
			receivers.getOrDefault(channel, List.of()).forEach(receiver -> receiver.accept(message));
		}

		@Override
		public @NotNull String name() {
			return "memory:" + name;
		}

		@Override
		public void publish(@NotNull String channel, byte @NotNull [] message) {
			deliver(this, channel, message);
		}

		@Override
		public void subscribe(@NotNull String channel, @NotNull Consumer<byte[]> receiver) {
			receivers.computeIfAbsent(channel, ignored -> new CopyOnWriteArrayList<>()).add(receiver);
		}

		@Override
		public int maxMessageSize() {
			return maxMessageSize;
		}

		@Override
		public boolean isConnected() {
			return connected;
		}

		@Override
		public void onReconnect(@NotNull Runnable listener) {
			reconnectListeners.add(listener);
		}
	}
}
