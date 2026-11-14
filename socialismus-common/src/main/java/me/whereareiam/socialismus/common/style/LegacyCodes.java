package me.whereareiam.socialismus.common.style;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Writes the legacy codes of a text, such as {@code &c}, {@code &l} and {@code &#ff0000}, as the tags they stand
 * for, so a style reads them like any tag. A code whose tag the style does not allow stays as written, and so does
 * {@code &r}, since no style may reset the format around a message.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class LegacyCodes {
	private static final Pattern CODE = Pattern.compile("[&§](#[0-9a-fA-F]{6}|[0-9a-fA-Fk-oK-O])");

	private static final Map<Character, String> TAGS = Map.ofEntries(
			Map.entry('0', "black"),
			Map.entry('1', "dark_blue"),
			Map.entry('2', "dark_green"),
			Map.entry('3', "dark_aqua"),
			Map.entry('4', "dark_red"),
			Map.entry('5', "dark_purple"),
			Map.entry('6', "gold"),
			Map.entry('7', "gray"),
			Map.entry('8', "dark_gray"),
			Map.entry('9', "blue"),
			Map.entry('a', "green"),
			Map.entry('b', "aqua"),
			Map.entry('c', "red"),
			Map.entry('d', "light_purple"),
			Map.entry('e', "yellow"),
			Map.entry('f', "white"),
			Map.entry('k', "obfuscated"),
			Map.entry('l', "bold"),
			Map.entry('m', "strikethrough"),
			Map.entry('n', "underlined"),
			Map.entry('o', "italic")
	);

	private static final Map<Character, StyleTag> DECORATIONS = Map.of(
			'k', StyleTag.OBFUSCATED,
			'l', StyleTag.BOLD,
			'm', StyleTag.STRIKETHROUGH,
			'n', StyleTag.UNDERLINED,
			'o', StyleTag.ITALIC
	);

	/**
	 * Writes the legacy codes of a text as tags.
	 *
	 * @param text    what a player wrote
	 * @param allowed the tags of the style
	 * @return the text with tags in place of the codes the style allows
	 */
	static @NotNull String toTags(@NotNull String text, @NotNull Set<StyleTag> allowed) {
		Matcher matcher = CODE.matcher(text);

		return matcher.replaceAll(match -> {
			String code = match.group(1);
			if (!allowed.contains(styleTagOf(code))) return Matcher.quoteReplacement(match.group());

			return Matcher.quoteReplacement("<" + tagOf(code) + ">");
		});
	}

	private static StyleTag styleTagOf(String code) {
		if (code.startsWith("#")) return StyleTag.COLOR;

		return DECORATIONS.getOrDefault(Character.toLowerCase(code.charAt(0)), StyleTag.COLOR);
	}

	private static String tagOf(String code) {
		if (code.startsWith("#")) return code.toLowerCase();

		return TAGS.get(Character.toLowerCase(code.charAt(0)));
	}
}
