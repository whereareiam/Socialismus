package me.whereareiam.socialismus.input.sync;

import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;

public interface ChatSyncBus {
	/**
	 * Publish a LOCAL message to the network.
	 */
	void publish(FormattedChatMessage message);

	/**
	 * Call once at startup to begin listening.
	 */
	void subscribe();
}
