package me.whereareiam.socialismus.model.chat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.configura.annotation.merge.Merge;
import me.whereareiam.configura.type.merge.WhenPresent;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;

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
	 * Players turning chats off for themselves.
	 */
	private Toggle toggle;


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
		 * Who may clear messages from chat history: the players who meet it see the clear button on the messages
		 * they receive. Kept as written, so a group of one's own takes none of the default's requirements.
		 */
		@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
		private RequirementGroup requirements;

		/**
		 * Whose messages nobody clears: the messages of a sender who meets it carry no clear button, and
		 * {@code /socialismus clear <player>} refuses to clear them. Kept as written, like {@link #requirements}.
		 */
		@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
		private RequirementGroup bypassRequirements;
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

	/**
	 * Players turning chats off for themselves, with {@code /socialismus toggle <chat>}.
	 */
	@Getter
	@Setter
	@ToString
	public static class Toggle {
		/**
		 * Whether players may turn chats off.
		 */
		private boolean enabled;

		/**
		 * The senders whose messages still reach players who turned the chat off; nobody when absent.
		 */
		private RequirementGroup bypass;
	}
}
