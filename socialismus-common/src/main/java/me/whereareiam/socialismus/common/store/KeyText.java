package me.whereareiam.socialismus.common.store;

import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.service.SerializationService;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

/**
 * Writes a store's keys as the text replication and persistence key their values by: the key's JSON, without the
 * quotes around a text, so that a {@code UUID} or a name reads as itself.
 *
 * @param <K> key type
 */
@RequiredArgsConstructor
final class KeyText<K> {
	private final Class<K> type;
	private final SerializationService serialization;

	@NotNull String text(@NotNull K key) {
		if (key instanceof String text) return text;

		String json = new String(serialization.serialize(key), StandardCharsets.UTF_8);
		boolean quoted = json.length() >= 2 && json.startsWith("\"") && json.endsWith("\"");

		return quoted ? json.substring(1, json.length() - 1) : json;
	}

	@NotNull K parse(@NotNull String text) {
		if (type == String.class) return type.cast(text);

		try {
			return serialization.deserialize(("\"" + text + "\"").getBytes(StandardCharsets.UTF_8), type);
		} catch (RuntimeException notAText) {
			return serialization.deserialize(text.getBytes(StandardCharsets.UTF_8), type);
		}
	}
}
