package me.whereareiam.socialismus.module.dialogue.api.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * What Dialogue tells players, {@code messages} in its working directory, grouped by the areas of
 * {@link DialogueSettings}. How a private message itself looks is in {@link MessagingFormats}.
 */
@Getter
@Setter
@ToString
public class DialogueMessages {
	/**
	 * What a player is told when a private message cannot be sent.
	 */
	private Messaging messaging;

	private Control control;

	/**
	 * What a player is told when a private message cannot be sent.
	 */
	@Getter
	@Setter
	@ToString
	public static class Messaging {
		/**
		 * The recipient is not online; {@code {recipientName}} names them.
		 */
		private String noRecipient;

		/**
		 * The sender wrote to themselves.
		 */
		private String samePlayer;

		/**
		 * The requirements of {@code messaging} do not let the sender write to the recipient;
		 * {@code {recipientName}} names them.
		 */
		private String notAllowed;

		/**
		 * What a player is told when {@code /reply} has nobody to answer.
		 */
		private Reply reply;

		/**
		 * What a player is told on entering and leaving conversation mode.
		 */
		private ConversationMode conversationMode;

		/**
		 * What a player is told when {@code /reply} has nobody to answer.
		 */
		/**
		 * The notices of conversation mode, with {@code {recipientName}} filled in.
		 */
		@Getter
		@Setter
		@ToString
		public static class ConversationMode {
			private String entered;

			private String left;

			/**
			 * Told when {@code messaging.conversationMode} is off and the command names no message.
			 */
			private String unavailable;
		}

		@Getter
		@Setter
		@ToString
		public static class Reply {
			/**
			 * The player has no conversation at all.
			 */
			private String noConversations;

			/**
			 * The player has no conversation recent enough to answer; their recent conversations follow.
			 */
			private String noPartner;

			/**
			 * The partner went offline and replies to offline players are off; {@code {partner}} names them.
			 */
			private String partnerOffline;

			/**
			 * Each recent conversation listed after {@link #noPartner}; {@code {index}} numbers it and
			 * {@code {partner}} names the other participant.
			 */
			private String conversation;
		}
	}

	/**
	 * The notices of what players decide about their own private messages.
	 */
	@Getter
	@Setter
	@ToString
	public static class Control {
		private Toggle toggle;

		private Ignore ignore;

		/**
		 * The notices of turning private messages off, with {@code {recipientName}} in {@code blocked}.
		 */
		@Getter
		@Setter
		@ToString
		public static class Toggle {
			private String off;

			private String on;

			private String blocked;

			private String unavailable;
		}

		/**
		 * The notices of ignoring, with {@code {ignoredName}} filled in; {@code {playerName}} is the player who reads them, as in every message.
		 */
		@Getter
		@Setter
		@ToString
		public static class Ignore {
			private String added;

			private String removed;

			private String blocked;

			private String exempt;

			private String unavailable;

			private Listing list;

			/**
			 * The list of ignored players: a header, one entry per player with {@code {ignoredName}}, or the text for
			 * an empty list.
			 */
			@Getter
			@Setter
			@ToString
			public static class Listing {
				private String header;

				private String entry;

				private String empty;
			}
		}
	}
}
