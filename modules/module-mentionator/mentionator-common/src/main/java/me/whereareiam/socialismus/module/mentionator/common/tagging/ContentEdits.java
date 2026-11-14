package me.whereareiam.socialismus.module.mentionator.common.tagging;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads a message's content as the text the sender wrote and changes parts of it by their place in that text,
 * keeping the style around each part. A part is changed only where it lies in the text of one component, as the text
 * a player types does.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ContentEdits {
	/**
	 * Returns the text of a content, component after component.
	 *
	 * @param content the content
	 * @return its text
	 */
	public static @NotNull String plain(@NotNull Component content) {
		StringBuilder text = new StringBuilder();
		append(content, text);

		return text.toString();
	}

	/**
	 * Returns whether a part of a content's text lies in the text of one component, so it can be changed alone.
	 *
	 * @param content the content
	 * @param start   where the part starts in the text
	 * @param end     where it ends, exclusive
	 * @return whether it can be changed
	 */
	public static boolean changeable(@NotNull Component content, int start, int end) {
		List<int[]> spans = new ArrayList<>();
		spans(content, new int[]{0}, spans);

		return spans.stream().anyMatch(span -> span[0] <= start && end <= span[1]);
	}

	/**
	 * Changes parts of a content.
	 *
	 * @param content the content
	 * @param edits   the changes, in the order of the text, none overlapping another and each changeable
	 * @return the changed content
	 */
	public static @NotNull Component apply(@NotNull Component content, @NotNull List<Edit> edits) {
		if (edits.isEmpty()) return content;

		return rebuild(content, new int[]{0}, edits);
	}

	private static void append(Component node, StringBuilder text) {
		text.append(own(node));
		node.children().forEach(child -> append(child, text));
	}

	private static void spans(Component node, int[] offset, List<int[]> spans) {
		String own = own(node);
		if (node instanceof TextComponent) spans.add(new int[]{offset[0], offset[0] + own.length()});
		offset[0] += own.length();
		node.children().forEach(child -> spans(child, offset, spans));
	}

	private static Component rebuild(Component node, int[] offset, List<Edit> edits) {
		int start = offset[0];
		String own = own(node);
		offset[0] += own.length();

		List<Component> children = new ArrayList<>();
		for (Component child : node.children()) children.add(rebuild(child, offset, edits));
		if (!(node instanceof TextComponent text)) return node.children(children);

		List<Edit> inside = edits.stream()
				.filter(edit -> start <= edit.start() && edit.end() <= start + own.length())
				.toList();
		if (inside.isEmpty()) return node.children(children);

		List<Component> pieces = new ArrayList<>();
		int cursor = 0;
		for (Edit edit : inside) {
			int at = edit.start() - start;
			if (at > cursor) pieces.add(Component.text(own.substring(cursor, at)));
			if (edit.replacement() != null) pieces.add(edit.replacement());
			cursor = edit.end() - start;
		}
		if (cursor < own.length()) pieces.add(Component.text(own.substring(cursor)));
		pieces.addAll(children);

		return text.content("").children(pieces);
	}

	// A component's own text, without its children's.
	private static String own(Component node) {
		if (node instanceof TextComponent text) return text.content();

		return PlainTextComponentSerializer.plainText().serialize(node.children(List.of()));
	}

	/**
	 * A part of the text and what takes its place.
	 *
	 * @param start       where it starts in the text
	 * @param end         where it ends, exclusive
	 * @param replacement what takes its place, {@code null} to remove it
	 */
	public record Edit(int start, int end, @Nullable Component replacement) {
	}
}
