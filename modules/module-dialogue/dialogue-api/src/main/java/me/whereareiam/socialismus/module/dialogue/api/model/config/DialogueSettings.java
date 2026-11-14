package me.whereareiam.socialismus.module.dialogue.api.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.configura.annotation.merge.Merge;
import me.whereareiam.configura.type.merge.WhenPresent;

import java.time.Duration;
import java.util.List;

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
		 * The styles of {@code styles.yml} whose tags a sender may use in private messages, by id, from lowest to
		 * highest: the sender gets the last one whose requirements they meet, checked on the sender's server.
		 * Without one, what a sender writes stays plain text.
		 */
		@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
		private List<String> styles = List.of();

		/**
		 * What the sender must meet to send a private message, checked with the part sender on the sender's server.
		 */
		@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
		private Requirements requires = Requirements.none();

		/**
		 * What the recipient must meet to receive a private message, checked with the parts recipient and sender. A
		 * recipient on another server, whom this server cannot check, meets only an empty list.
		 */
		@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
		private Requirements reaches = Requirements.none();

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
		 * With SocialismusNetwork, how long to wait for the node a recipient is on to confirm a private message before
		 * the sender is told the recipient is not online.
		 */
		private Duration confirmation;

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
			 * The senders whose messages still reach players who turned them off, checked with the part sender; an
			 * empty list lets everyone's through.
			 */
			@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
			private Requirements bypass = Requirements.none();
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
			 * The players who cannot be ignored, such as staff, checked with the part sender; an empty list exempts
			 * everyone.
			 */
			@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
			private Requirements exempt = Requirements.none();
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
			 * The players whose private messages are never shown to spies, checked with the part sender; an empty list
			 * hides everyone's.
			 */
			@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
			private Requirements exempt = Requirements.none();
		}
	}
}
