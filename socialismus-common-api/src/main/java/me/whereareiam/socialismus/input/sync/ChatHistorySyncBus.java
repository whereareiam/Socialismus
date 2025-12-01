package me.whereareiam.socialismus.input.sync;

import me.whereareiam.socialismus.input.event.chat.history.ChatHistoryRemoveEvent;

public interface ChatHistorySyncBus {
	/**
	 * Publish a LOCAL event to the network.
	 *
	 * @param event The event to publish.
	 */
	void publish(ChatHistoryRemoveEvent event);

	/**
	 * Call once at startup to begin listening.
	 */
	void subscribe();
}

