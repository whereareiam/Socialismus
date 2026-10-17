package me.whereareiam.socialismus.model.chat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * What chats do when the server syncs with others, {@code chat/sync} in the data directory. Whether the server
 * syncs at all is in {@link me.whereareiam.socialismus.model.config.Sync}.
 */
@Getter
@Setter
@ToString
public class ChatSync {
	/**
	 * Whether chat messages are sent to and received from the other servers.
	 */
	private boolean enabled;

	/**
	 * Whether a message from another server keeps the format it was given there.
	 */
	private boolean preserveFormat;

	/**
	 * Whether clearing the chat clears it on the other servers too.
	 */
	private boolean clearHistory;
}
