package me.whereareiam.socialismus.model.chat;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Configuration model for chat system settings.
 * Contains global settings that control chat behavior, notifications,
 * fallback options, and chat history management.
 *
 * <p>This singleton class stores configuration values that are
 * loaded from the plugin's configuration file.</p>
 */
@Getter
@Setter
@ToString
@Singleton
public class ChatSettings {
	/**
	 * Whether to notify when no chat channel is found.
	 */
	private boolean notifyNoChat;

	/**
	 * Whether to notify when no format is found.
	 */
	private boolean notifyNoFormat;

	/**
	 * Whether to notify when no players are available.
	 */
	private boolean notifyNoPlayers;

	/**
	 * Whether to notify when no nearby players are found.
	 */
	private boolean notifyNoNearbyPlayers;

	/**
	 * Settings for fallback chat behavior.
	 */
	private FallbackChatSettings fallback;

	/**
	 * Settings for chat history management.
	 */
	private ChatHistorySettings history;

	/**
	 * Whether to synchronize chat messages across servers.
	 */
	private SynchronizationSettings synchronization;

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
		private String chatId;
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

	@Getter
	@Setter
	@ToString
	public static class SynchronizationSettings {
		private boolean enabled;
		private boolean preserveFormat;
		private boolean clearHistory;
	}
}