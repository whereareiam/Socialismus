package me.whereareiam.socialismus.module.mentionator.api.model.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.configura.annotation.merge.Merge;
import me.whereareiam.configura.type.merge.WhenPresent;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Mentionator's {@code settings}: how many tags a message holds and how often a player is notified.
 */
@Getter
@Setter
@ToString
public class MentionatorSettings {
	private Limits limits = new Limits();

	/**
	 * How much tagging Mentionator allows.
	 */
	@Getter
	@Setter
	@ToString
	public static class Limits {
		/**
		 * How many tags one message may hold: the last entry the sender meets counts, and tags past it stay plain
		 * text. A tag counts once each time it is written, however many players it tags.
		 */
		private List<PerMessage> perMessage = new ArrayList<>();

		/**
		 * How many notifications a player gets in a while; tags past it are highlighted but silent.
		 */
		private Received received = new Received();

		/**
		 * How often one sender notifies the same player at most.
		 */
		private Duration repeat;

		/**
		 * Whether players may tag themselves.
		 */
		private boolean self;
	}

	/**
	 * A limit of tags per message, for the senders who meet its list.
	 */
	@Getter
	@ToString
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PerMessage {
		/**
		 * How many tags a message may hold; {@code -1} for any number.
		 */
		private int limit;

		/**
		 * What the sender must meet for this limit, checked with the parts sender and message.
		 */
		@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
		private Requirements requires = Requirements.none();
	}

	/**
	 * How many notifications a player gets in a while.
	 */
	@Getter
	@Setter
	@ToString
	public static class Received {
		/**
		 * How many notifications; {@code -1} for any number.
		 */
		private int count;

		/**
		 * The while.
		 */
		private Duration per;

		/**
		 * What a sender must meet for their tags to notify a player past the limit, checked with the parts sender
		 * and message.
		 */
		@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
		private Requirements bypass = Requirements.none();
	}
}
