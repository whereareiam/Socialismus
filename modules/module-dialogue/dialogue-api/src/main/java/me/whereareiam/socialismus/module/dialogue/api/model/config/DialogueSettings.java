package me.whereareiam.socialismus.module.dialogue.api.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.type.chat.Participants;

import java.time.Duration;
import java.util.Map;

/**
 * How Dialogue behaves, {@code settings} in its working directory, grouped by area.
 */
@Getter
@Setter
@ToString
public class DialogueSettings {
	/**
	 * Who may message whom, and how replies and conversations work.
	 */
	private Messaging messaging;

	/**
	 * Who may message whom, and how replies and conversations work.
	 */
	@Getter
	@Setter
	@ToString
	public static class Messaging {
		/**
		 * What the sender ({@link Participants#SENDER}) and the recipient ({@link Participants#RECIPIENT}) must
		 * meet for a private message between them; a participant without a group meets it.
		 */
		private Map<Participants, RequirementGroup> requirements;

		/**
		 * Which conversation {@code /reply} answers.
		 */
		private Reply reply;

		/**
		 * How long conversations are kept, and how many.
		 */
		private Conversations conversations;

		/**
		 * Whether {@code /message <player>} without a message makes the sender's chat go to that player until they
		 * leave with the same command.
		 */
		private boolean conversationMode;

		/**
		 * Which conversation {@code /reply} answers.
		 */
		@Getter
		@Setter
		@ToString
		public static class Reply {
			/**
			 * How long after its latest message a conversation can still be answered.
			 */
			private Duration timeout;

			/**
			 * Whether {@code /reply} still writes to a partner who went offline.
			 */
			private boolean toOffline;
		}

		/**
		 * How long conversations are kept, and how many.
		 */
		@Getter
		@Setter
		@ToString
		public static class Conversations {
			/**
			 * How long a conversation is kept after its latest message.
			 */
			private Duration ttl;

			/**
			 * How many conversations a player keeps; the least recently active go first.
			 */
			private int maxConversations;

			/**
			 * How many messages a conversation keeps; the oldest go first.
			 */
			private int maxMessages;
		}
	}
}
