package me.whereareiam.socialismus.common.serializer.legacy;

import lombok.experimental.UtilityClass;

import java.util.Map;

/**
 * Converts vanilla legacy (§ colour / format) codes that may appear
 * inside a MiniMessage string into proper MiniMessage tags.
 *
 * <p>The converter is <strong>stateless</strong> and very fast
 * (single pass, no regex) so it is safe to run on every chat packet
 * when {@code allowLegacyParsing} is enabled.</p>
 *
 * <p>Supported:</p>
 * <ul>
 *   <li>Standard colour codes §0 – §f</li>
 *   <li>Formatting codes §k – §o</li>
 *   <li>Reset code §r</li>
 *   <li>Hex codes of the form §x§R§R§G§G§B§B (1.16+)</li>
 *   <li>The same sequence with <strong>&amp;</strong> instead of §</li>
 * </ul>
 */
@UtilityClass
public class LegacyToMiniMessage {
	private static final Map<Character, String> NAMED_COLOURS = Map.ofEntries(
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
			Map.entry('f', "white")
	);

	private static final Map<Character, String> FORMATTERS = Map.ofEntries(
			Map.entry('k', "obfuscated"),
			Map.entry('l', "bold"),
			Map.entry('m', "strikethrough"),
			Map.entry('n', "underlined"),
			Map.entry('o', "italic")
	);

	/**
	 * Replace every legacy code inside {@code input} with an equivalent
	 * MiniMessage tag so that the final string can be deserialised by
	 * {@code MiniMessage}.
	 */
	public static String convert(String input) {
		if (input == null || input.isEmpty()) return input;

		StringBuilder out = new StringBuilder(input.length() + 32);
		char[] arr = input.toCharArray();

		for (int i = 0; i < arr.length; i++) {
			char c = arr[i];

			if ((c == '§' || c == '&') && i + 1 < arr.length) {
				char code = Character.toLowerCase(arr[i + 1]);

				/* ---------- hex colour §x§R§R§G§G§B§B --------- */
				if (code == 'x' && i + 13 < arr.length) {
					StringBuilder hex = new StringBuilder(6);
					boolean valid = true;
					for (int j = 0; j < 6; j++) {
						if (arr[i + 2 + j * 2] != c) { // must be same prefix
							valid = false;
							break;
						}
						hex.append(arr[i + 3 + j * 2]);
					}
					if (valid) {
						out.append("<color:#").append(hex).append('>');
						i += 13; // skip "§x§R§R§G§G§B§B"
						continue;
					}
				}

				/* ---------- named colours ---------- */
				if (NAMED_COLOURS.containsKey(code)) {
					out.append('<').append(NAMED_COLOURS.get(code)).append('>');
					i++; // skip the colour code character
					continue;
				}

				/* ---------- text formatters ---------- */
				if (FORMATTERS.containsKey(code)) {
					out.append('<').append(FORMATTERS.get(code)).append('>');
					i++;
					continue;
				}

				/* ---------- reset ---------- */
				if (code == 'r') {
					out.append("<reset>");
					i++;
					continue;
				}
			}

			out.append(c);
		}
		return out.toString();
	}
}
