package me.whereareiam.socialismus.module.mentionator.api.model.tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

/**
 * How a tag is matched and counted; a setting left out takes its default.
 */
@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TagParameters {
	/**
	 * How long a value must be for a bare form, such as {@code {username}} without an {@code @}, to tag; so short
	 * names in ordinary words tag nobody. Any length when absent.
	 */
	private @Nullable Integer minimumLength;

	/**
	 * How often the tag counts in one message; the rest stay plain text. Any number when absent.
	 */
	private @Nullable Integer perMessage;

	/**
	 * How often each player's value of a {@code %placeholder%} form is read again, besides when they join or change
	 * servers; every minute when absent.
	 */
	private @Nullable Duration refresh;

	/**
	 * Whether the tag names the player it tags, so it notifies players who chose to hear only direct tags; true for a
	 * form with {@code {username}} when absent.
	 */
	private @Nullable Boolean direct;
}
