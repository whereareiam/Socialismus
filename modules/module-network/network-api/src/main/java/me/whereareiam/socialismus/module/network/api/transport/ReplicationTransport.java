package me.whereareiam.socialismus.module.network.api.transport;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Carries messages between the nodes of the network: what SocialismusRedis, SocialismusChannelizer or another module
 * registers under {@link me.whereareiam.socialismus.module.network.api.NetworkResources#TRANSPORT}. SocialismusNetwork
 * uses one transport; it only sends and receives bytes and leaves what they mean to the network.
 *
 * <p>A transport delivers a message to every node subscribed to its channel, the sender included or not; it may lose
 * messages while it is disconnected, and tells the network when it is connected again, so the network can catch up.
 * Messages from one sender arrive in the order they were sent.</p>
 */
public interface ReplicationTransport {
	/**
	 * Returns the transport's name, for log messages, such as {@code Redis}.
	 *
	 * @return the transport's name
	 */
	@NotNull String name();

	/**
	 * Sends a message to every node subscribed to a channel.
	 *
	 * @param channel the channel
	 * @param message the message
	 */
	void publish(@NotNull String channel, byte @NotNull [] message);

	/**
	 * Receives the messages sent to a channel, on a thread of the transport.
	 *
	 * @param channel  the channel
	 * @param receiver called with each message
	 */
	void subscribe(@NotNull String channel, @NotNull Consumer<byte[]> receiver);

	/**
	 * Returns the largest message, in bytes, the transport carries. The network splits what it sends to fit, and
	 * drops a single change that does not, with a warning. By default there is no limit.
	 *
	 * @return the largest message in bytes
	 */
	default int maxMessageSize() {
		return Integer.MAX_VALUE;
	}

	/**
	 * Returns whether messages are carried right now.
	 *
	 * @return whether messages are carried right now
	 */
	boolean isConnected();

	/**
	 * Calls a listener each time the transport is connected again after it was not; messages sent while it was not
	 * connected may have been lost.
	 *
	 * @param listener the listener
	 */
	void onReconnect(@NotNull Runnable listener);
}
