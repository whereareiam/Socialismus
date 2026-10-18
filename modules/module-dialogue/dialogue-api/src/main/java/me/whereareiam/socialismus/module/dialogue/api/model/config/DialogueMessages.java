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
		 * What a player is told when {@code /reply} has nobody to answer.
		 */
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
}
