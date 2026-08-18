package me.whereareiam.socialismus.module.channelizer.api;

import me.whereareiam.socialismus.service.resource.sync.SyncSubscriber;
import org.jetbrains.annotations.NotNull;

/**
 * Transport-specific message bus abstraction used by Channelizer to bridge
 * Socialismus synchronization payloads across the underlying network layer.
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
	 * Subscribes a listener to payloads arriving on the given logical channel.
	 *
	 * @param channel the logical channel name
	 * @param subscriber the subscriber to notify
	 */
	void subscribe(@NotNull String channel, @NotNull SyncSubscriber subscriber);
}
