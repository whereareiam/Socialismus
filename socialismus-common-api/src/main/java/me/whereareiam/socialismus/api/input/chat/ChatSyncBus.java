package me.whereareiam.socialismus.api.input.chat;

import me.whereareiam.socialismus.api.model.chat.message.ChatMessage;

public interface ChatSyncBus {
	/**
	 * Publish a LOCAL message to the network.
	 */
	void publish(ChatMessage message);

	/**
	 * Call once at startup to begin listening.
	 */
	void startListening();
}
