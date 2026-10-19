package me.whereareiam.socialismus.module.channelizer.api;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * The plugin messages SocialismusChannelizer carries SocialismusNetwork's messages in, as the platform sends and
 * receives them.
 */
public interface PlatformMessageBus {
	/**
	 * Publishes a synchronization payload to the given logical channel.
	 *
	 * @param channel the logical channel name
	 * @param payload the raw payload bytes to forward
	 */
	void publish(@NotNull String channel, byte @NotNull [] payload);

	/**
	 * Subscribes a receiver to payloads arriving on the given logical channel.
	 *
	 * @param channel  the logical channel name
	 * @param receiver called with each payload
	 */
	void subscribe(@NotNull String channel, @NotNull Consumer<byte[]> receiver);

	/**
	 * Returns the largest payload, in bytes, one message carries.
	 */
	int maxPayloadSize();

	/**
	 * Returns whether a message can be sent right now: plugin messages travel through a player's connection, so a
	 * server without players can neither send nor receive.
	 */
	boolean canSend();

	/**
	 * Calls a listener each time messages can be sent again after they could not, such as when the first player
	 * joins an empty server.
	 *
	 * @param listener the listener
	 */
	void onSendable(@NotNull Runnable listener);
}
