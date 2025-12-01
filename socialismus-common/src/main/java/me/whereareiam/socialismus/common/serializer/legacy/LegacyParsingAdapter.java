package me.whereareiam.socialismus.common.serializer.legacy;

import lombok.experimental.UtilityClass;
import me.whereareiam.socialismus.type.SerializationType;
import me.whereareiam.socialismus.util.ComponentUtil;
import net.kyori.adventure.text.Component;

/**
 * Converts a raw string that may contain legacy (§ or &amp;) colour codes
 * into a string that can be safely deserialised by the <em>target</em>
 * {@link SerializationType}.
 *
 * <p>The adapter contains <em>no mutable state</em>; it can therefore be
 * cached or reused freely.</p>
 */
@UtilityClass
public class LegacyParsingAdapter {
	public static String transform(String input, SerializationType target) {
		if (input == null || input.isEmpty())
			return input;

		switch (target) {
			case MINIMESSAGE -> {
				// Replace legacy codes with equivalent MiniMessage tags
				return LegacyToMiniMessage.convert(input);
			}

			case GSON, GSON_DOWNSAMPLING -> {
				// Step 1: §‑deserialize → Component
				Component legacy = ComponentUtil.getLEGACY_SECTION_SERIALIZER().deserialize(input);

				// Step 2: Component → JSON string understood by target
				return target.getSerializer().serialize(legacy);
			}

			default -> {
				return input;
			}
		}
	}
}
