package me.whereareiam.socialismus.module.mentionator.api.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.module.mentionator.api.type.Mode;

import java.util.EnumMap;
import java.util.Map;

/**
 * What Mentionator tells players, {@code messages} in its working directory.
 */
@Getter
@Setter
@ToString
public class MentionatorMessages {
	private Mentions mentions = new Mentions();

	private Limits limits = new Limits();

	/**
	 * What {@code /socialismus mentions} shows.
	 */
	@Getter
	@Setter
	@ToString
	public static class Mentions {
		/**
		 * The player's mode and the tags they may use: {@code {mode}} and {@code {tags}}.
		 */
		private String overview;

		/**
		 * One tag of {@code {tags}}: {@code {tag}}, and {@code {written}} for how it is written.
		 */
		private String tag;

		/**
		 * What stands between two tags of {@code {tags}}.
		 */
		private String separator;

		/**
		 * {@code {tags}} when the player may use no tag.
		 */
		private String noTags;

		/**
		 * The player chose a mode: {@code {mode}}.
		 */
		private String changed;

		/**
		 * How each mode is named in {@code {mode}}.
		 */
		private Map<Mode, String> modes = new EnumMap<>(Mode.class);
	}

	/**
	 * What a sender is told when a limit of {@code settings} holds their tags back.
	 */
	@Getter
	@Setter
	@ToString
	public static class Limits {
		/**
		 * The message held more tags than {@code limits.perMessage} allows: {@code {limit}}.
		 */
		private String perMessage;
	}
}
