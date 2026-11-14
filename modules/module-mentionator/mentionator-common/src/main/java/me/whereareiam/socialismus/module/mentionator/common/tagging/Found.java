package me.whereareiam.socialismus.module.mentionator.common.tagging;

import me.whereareiam.socialismus.module.mentionator.api.model.tag.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A tag found in a message's text, before its lists decide whom it tags.
 *
 * @param tag        the tag
 * @param form       the form it was written in
 * @param start      where it starts in the text
 * @param end        where it ends in the text, exclusive
 * @param match      what it matched in its own spelling: the player's name, the value, or the form
 * @param candidates the players it names: those of the name or value, or every candidate for a literal form
 * @param escaped    whether a backslash before it keeps it plain text, such as {@code \@everyone}
 */
public record Found(
		@NotNull Tag tag,
		@NotNull Form form,
		int start,
		int end,
		@NotNull String match,
		@NotNull List<Candidate> candidates,
		boolean escaped
) {
	public Found {
		candidates = List.copyOf(candidates);
	}
}
