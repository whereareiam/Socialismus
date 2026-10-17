package me.whereareiam.socialismus.model.chat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The settings of chatting, {@code chat/settings} in the data directory: notifications, the fallback chat and chat
 * history. What chats do when synced is in {@link ChatSync}.
 */
@Getter
@Setter
@ToString
public class ChatSettings {
	/**
	 * What players are told when their message goes nowhere.
	 */
	private Notifications notifications;

	/**
	 * Settings for fallback chat behavior.
	 */
	private FallbackChatSettings fallback;

	/**
	 * Settings for chat history management.
	 */
	private ChatHistorySettings history;


	/**
	 * Configuration class for fallback chat settings.
	 * Defines behavior when primary chat channels are unavailable.
	 */
	@Getter
	@Setter
	@ToString
	public static class FallbackChatSettings {
		/**
		 * Whether fallback chat is enabled.
		 */
		private boolean enabled;

		/**
		 * The identifier of the fallback chat channel.
		 */
		private String chat;
	}

	/**
	 * Configuration class for chat history settings.
	 * Controls how chat history is managed and accessed.
	 */
	@Getter
	@Setter
	@ToString
	public static class ChatHistorySettings {
		/**
		 * Size of the chat filler.
		 */
		private int fillerSize;

		/**
		 * Maximum size of chat history to maintain.
		 */
		private int historySize;

		/**
		 * Permission required to access chat history.
		 */
		private String permission;

		/**
		 * Permission to bypass chat history restrictions.
		 */
		private String bypassPermission;
	}

	/**
	 * What players are told when their message goes nowhere.
	 */
	@Getter
	@Setter
	@ToString
	public static class Notifications {
		/**
		 * Whether to tell the sender that no chat matched.
		 */
		private boolean noChat;

		/**
		 * Whether to tell the sender that no format matched.
		 */
		private boolean noFormat;

		/**
		 * Whether to tell the sender that no other player is online.
		 */
		private boolean noPlayers;

		/**
		 * Whether to tell the sender that no player is within the chat's radius.
		 */
		private boolean noNearbyPlayers;

		/**
		 * Whether to tell the sender that a trigger was used without any message.
		 */
		private boolean emptyMessage;
	}
}
