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
	 * What players decide about their own private messages.
	 */
	private Control control;

	/**
	 * What staff see of private messages.
	 */
	private Moderation moderation;

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

	/**
	 * What players decide about their own private messages.
	 */
	@Getter
	@Setter
	@ToString
	public static class Control {
		private Toggle toggle;

		private Ignore ignore;

		/**
		 * Turning private messages off.
		 */
		@Getter
		@Setter
		@ToString
		public static class Toggle {
			/**
			 * Whether players may turn private messages off.
			 */
			private boolean enabled;

			/**
			 * The senders whose messages still reach players who turned them off; nobody when absent.
			 */
			private RequirementGroup bypass;
		}

		/**
		 * Ignoring players.
		 */
		@Getter
		@Setter
		@ToString
		public static class Ignore {
			/**
			 * Whether players may ignore others.
			 */
			private boolean enabled;

			/**
			 * Whether an ignored player's public chat is hidden as well as their private messages.
			 */
			private boolean includesChat;

			/**
			 * The players who cannot be ignored, such as staff; nobody when absent.
			 */
			private RequirementGroup exempt;
		}
	}

	/**
	 * What staff see of private messages.
	 */
	@Getter
	@Setter
	@ToString
	public static class Moderation {
		private Spy spy;

		/**
		 * Privacy mode: spying is unavailable to everyone. Dialogue keeps nothing it could expose later, and once it
		 * stores conversations in a database it encrypts them there.
		 */
		private boolean privacy;

		/**
		 * Spying on private messages between other players.
		 */
		@Getter
		@Setter
		@ToString
		public static class Spy {
			/**
			 * Whether staff may spy.
			 */
			private boolean enabled;

			/**
			 * The players whose private messages are never shown to spies; nobody when absent.
			 */
			private RequirementGroup exempt;
		}
	}
}
