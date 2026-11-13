package me.whereareiam.socialismus.common.style;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Turns the tag names of {@code styles.yml} into {@link StyleTag}s: a tag's own name, or a group of them.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class StyleTags {
	private static final Map<String, Set<StyleTag>> GROUPS = Map.of(
			"*", EnumSet.allOf(StyleTag.class),
			"decorations", EnumSet.of(
					StyleTag.BOLD,
					StyleTag.ITALIC,
					StyleTag.UNDERLINED,
					StyleTag.STRIKETHROUGH,
					StyleTag.OBFUSCATED
			),
			"gradients", EnumSet.of(StyleTag.GRADIENT, StyleTag.RAINBOW, StyleTag.TRANSITION, StyleTag.PRIDE)
	);

	private static final Map<String, StyleTag> BY_KEY = Arrays.stream(StyleTag.values())
			.collect(Collectors.toUnmodifiableMap(StyleTag::getKey, Function.identity()));

	private static final Set<String> NEVER = Set.of("click", "insert", "nbt", "selector", "score", "newline", "reset");

	/**
	 * Reads the tags of a style: its {@code tags} without its {@code excluded}.
	 *
	 * @param path     where the style is, such as {@code styles[1]}, for the failure
	 * @param tags     the names the style allows
	 * @param excluded the names it takes out again
	 * @return the tags
	 * @throws IllegalArgumentException naming the path of a name that is no tag or group, or of a tag no style may
	 *                                  allow
	 */
	static @NotNull Set<StyleTag> read(@NotNull String path, @NotNull List<String> tags, @NotNull List<String> excluded) {
		Set<StyleTag> allowed = EnumSet.noneOf(StyleTag.class);
		for (int index = 0; index < tags.size(); index++)
			allowed.addAll(named(path + ".tags[" + index + "]", tags.get(index)));

		for (int index = 0; index < excluded.size(); index++)
			allowed.removeAll(named(path + ".excluded[" + index + "]", excluded.get(index)));

		return allowed;
	}

	private static Set<StyleTag> named(String path, String name) {
		String key = name == null ? "" : name.trim().toLowerCase();
		if (NEVER.contains(key))
			throw new IllegalArgumentException(path + ": <" + key + "> is never read from what players write");

		Set<StyleTag> group = GROUPS.get(key);
		if (group != null) return group;

		StyleTag tag = BY_KEY.get(key);
		if (tag == null) throw new IllegalArgumentException(path + ": " + name + " is no tag or group of tags");

		return EnumSet.of(tag);
	}
}
