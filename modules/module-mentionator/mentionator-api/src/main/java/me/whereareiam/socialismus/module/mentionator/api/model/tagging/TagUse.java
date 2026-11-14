package me.whereareiam.socialismus.module.mentionator.api.model.tagging;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * One tag written in a message.
 *
 * @param tag     the tag's id, such as {@code everyone}
 * @param written the text as the sender wrote it, such as {@code @steve}
 * @param match   what it matched in its own spelling, such as {@code Steve}
 * @param targets the players it tags
 * @param formats the formats of the tag whose {@code requires} the sender met where the message was written, by their
 *                place in the tag's list, so every server shows the same ones
 */
public record TagUse(
		@NotNull String tag,
		@NotNull String written,
		@NotNull String match,
		@NotNull List<UUID> targets,
		@NotNull List<Integer> formats
) {
	public TagUse {
		targets = List.copyOf(targets);
		formats = List.copyOf(formats);
	}
}
