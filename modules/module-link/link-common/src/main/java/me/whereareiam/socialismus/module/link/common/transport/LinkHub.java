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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * The proxy's end of the link: it listens for its servers, lets in those that prove they know the secret, and relays
 * every message one of them sends to all the others, as well as to the proxy's own subscribers.
 */
public final class LinkHub implements ReplicationTransport, AutoCloseable {
	private static final Duration HANDSHAKE_TIMEOUT = Duration.ofSeconds(5);

	private final ServerSocket server;
	private final LinkAuthentication authentication;
	private final Set<Peer> peers = ConcurrentHashMap.newKeySet();
	private final Map<String, List<Consumer<byte[]>>> receivers = new ConcurrentHashMap<>();
	private volatile boolean closed;

	/**
	 * Starts listening for servers.
	 *
	 * @param address where to listen; port 0 picks a free one
	 * @param secret  the secret servers must know
	 * @throws IOException when the address cannot be listened on
	 */
	public LinkHub(@NotNull InetSocketAddress address, @NotNull String secret) throws IOException {
		this.authentication = new LinkAuthentication(secret);
		this.server = new ServerSocket();
		server.setReuseAddress(true);
		server.bind(address);

		Thread acceptor = new Thread(this::accept, "SocialismusLink hub");
		acceptor.setDaemon(true);
		acceptor.start();
	}

	/**
	 * Returns where the hub listens.
	 */
	public @NotNull InetSocketAddress address() {
		return (InetSocketAddress) server.getLocalSocketAddress();
	}

	/**
	 * Returns how many servers are connected.
	 */
	public int connected() {
		return peers.size();
	}

	@Override
	public @NotNull String name() {
		return "the link";
	}

	@Override
	public void publish(@NotNull String channel, byte @NotNull [] message) {
		byte[] frame = LinkFrames.message(channel, message);
		peers.forEach(peer -> peer.send(frame));
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
		return !closed;
	}

	@Override
	public void onReconnect(@NotNull Runnable listener) {
		// The hub never loses its servers all at once; each server catches up when it connects again.
	}

	@Override
	public void close() {
		closed = true;
		try {
			server.close();
		} catch (IOException ignored) {
			// Closing anyway.
		}
		peers.forEach(Peer::close);
	}

	private void accept() {
		while (!closed) {
			try {
				Socket socket = server.accept();
				Thread reader = new Thread(() -> serve(socket), "SocialismusLink peer " + socket.getRemoteSocketAddress());
				reader.setDaemon(true);
				reader.start();
			} catch (IOException failure) {
				if (!closed) Logger.warn("The link stopped accepting servers: %s", failure.getMessage());
				return;
			}
		}
	}

	private void serve(Socket socket) {
		Peer peer;
		try {
			socket.setTcpNoDelay(true);
			socket.setSoTimeout((int) HANDSHAKE_TIMEOUT.toMillis());
			DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

			byte[] challenge = LinkAuthentication.challenge();
			LinkFrames.write(out, challenge);
			if (!authentication.accepts(challenge, LinkFrames.read(in))) {
				Logger.warn("Refused a server at %s on the link: it does not know the secret", socket.getRemoteSocketAddress());
				socket.close();
				return;
			}

			LinkFrames.write(out, new byte[]{1});
			socket.setSoTimeout(0);
			peer = new Peer(socket, out);
			peers.add(peer);
			Logger.info("A server at %s joined the link", socket.getRemoteSocketAddress());

			while (!closed) {
				byte[] frame = LinkFrames.read(in);
				relay(peer, frame);
			}
		} catch (IOException failure) {
			closeQuietly(socket);
			if (!closed && !(failure instanceof SocketException)) Logger.debug("A server left the link: %s", failure.getMessage());
		} finally {
			peers.removeIf(candidate -> candidate.socket == socket);
		}
	}

	private void relay(Peer from, byte[] frame) throws IOException {
		LinkFrames.Message message = LinkFrames.Message.decode(frame);
		for (Peer peer : peers) {
			if (peer != from) peer.send(frame);
		}
		receivers.getOrDefault(message.channel(), List.of()).forEach(receiver -> {
			try {
				receiver.accept(message.payload());
			} catch (RuntimeException failure) {
				Logger.warn("A receiver of %s failed: %s", message.channel(), failure);
			}
		});
	}

	private static void closeQuietly(Socket socket) {
		try {
			socket.close();
		} catch (IOException ignored) {
			// Closing anyway.
		}
	}

	/**
	 * A server connected to the hub.
	 */
	private static final class Peer {
		private final Socket socket;
		private final DataOutputStream out;

		private Peer(Socket socket, DataOutputStream out) {
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
			closeQuietly(socket);
		}
	}
}
