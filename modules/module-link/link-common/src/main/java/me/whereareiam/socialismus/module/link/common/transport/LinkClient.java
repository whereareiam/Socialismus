package me.whereareiam.socialismus.module.link.common.transport;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.network.api.transport.ReplicationTransport;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * A server's end of the link: it connects to the proxy's hub, proves it knows the secret, and connects again whenever
 * the connection drops, telling the network once it is back.
 */
public final class LinkClient implements ReplicationTransport, AutoCloseable {
	private static final Duration RETRY = Duration.ofSeconds(2);
	private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);

	private final InetSocketAddress hub;
	private final LinkAuthentication authentication;
	private final Map<String, List<Consumer<byte[]>>> receivers = new ConcurrentHashMap<>();
	private final List<Runnable> reconnectListeners = new CopyOnWriteArrayList<>();
	private volatile Connection connection;
	private volatile boolean closed;

	/**
	 * Starts connecting to the hub, in the background.
	 *
	 * @param hub    where the proxy's hub listens
	 * @param secret the secret the hub asks for
	 */
	public LinkClient(@NotNull InetSocketAddress hub, @NotNull String secret) {
		this.hub = hub;
		this.authentication = new LinkAuthentication(secret);

		Thread thread = new Thread(this::run, "SocialismusLink client");
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public @NotNull String name() {
		return "the link";
	}

	@Override
	public void publish(@NotNull String channel, byte @NotNull [] message) {
		Connection current = connection;
		if (current != null) current.send(LinkFrames.message(channel, message));
	}

	@Override
	public void subscribe(@NotNull String channel, @NotNull Consumer<byte[]> receiver) {
		receivers.computeIfAbsent(channel, ignored -> new CopyOnWriteArrayList<>()).add(receiver);
	}

	@Override
	public int maxMessageSize() {
		return LinkFrames.MAX_FRAME - 1024;
	}

	@Override
	public boolean isConnected() {
		return connection != null && !closed;
	}

	@Override
	public void onReconnect(@NotNull Runnable listener) {
		reconnectListeners.add(listener);
	}

	@Override
	public void close() {
		closed = true;
		Connection current = connection;
		if (current != null) current.close();
	}

	private void run() {
		boolean everConnected = false;
		boolean warned = false;
		while (!closed) {
			try (Socket socket = new Socket()) {
				socket.setTcpNoDelay(true);
				socket.connect(hub, (int) CONNECT_TIMEOUT.toMillis());
				DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

				LinkFrames.write(out, authentication.answer(LinkFrames.read(in)));
				byte[] accepted = LinkFrames.read(in);
				if (accepted.length != 1 || accepted[0] != 1) throw new IOException("The hub did not accept this server");

				connection = new Connection(socket, out);
				Logger.info("Connected to the link at %s", hub);
				warned = false;
				if (everConnected) reconnectListeners.forEach(Runnable::run);
				everConnected = true;

				while (!closed) receive(LinkFrames.read(in));
			} catch (IOException failure) {
				if (!closed && !warned) Logger.warn("Not connected to the link at %s; retrying: %s", hub, failure.getMessage());
				warned = true;
			} finally {
				connection = null;
			}

			pause();
		}
	}

	private void receive(byte[] frame) throws IOException {
		LinkFrames.Message message = LinkFrames.Message.decode(frame);
		receivers.getOrDefault(message.channel(), List.of()).forEach(receiver -> {
			try {
				receiver.accept(message.payload());
			} catch (RuntimeException failure) {
				Logger.warn("A receiver of %s failed: %s", message.channel(), failure);
			}
		});
	}

	private void pause() {
		try {
			Thread.sleep(RETRY.toMillis());
		} catch (InterruptedException interrupted) {
			Thread.currentThread().interrupt();
			closed = true;
		}
	}

	/**
	 * The connection to the hub while it lasts.
	 */
	private static final class Connection {
		private final Socket socket;
		private final DataOutputStream out;

		private Connection(Socket socket, DataOutputStream out) {
			this.socket = socket;
			this.out = out;
		}

		private synchronized void send(byte[] frame) {
			try {
				LinkFrames.write(out, frame);
			} catch (IOException failure) {
				close();
			}
		}

		private void close() {
			try {
				socket.close();
			} catch (IOException ignored) {
				// Closing anyway.
			}
		}
	}
}
