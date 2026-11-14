package me.whereareiam.socialismus.module.network.common.chat;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.attribute.AttributeKey;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.service.SerializationService;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The attributes of chat messages that travel to the other nodes with the messages, by their full key, as modules
 * ask for them through {@link me.whereareiam.socialismus.module.network.api.NetworkService#carry}.
 */
public final class CarriedAttributes {
	private final Map<String, AttributeKey<?>> keys = new ConcurrentHashMap<>();

	/**
	 * Carries an attribute from now on.
	 *
	 * @param key the attribute's key
	 */
	public void carry(@NotNull AttributeKey<?> key) {
		keys.put(key.getFullKey(), key);
	}

	/**
	 * Stops carrying an attribute.
	 *
	 * @param key the attribute's key
	 */
	public void drop(@NotNull AttributeKey<?> key) {
		keys.remove(key.getFullKey());
	}

	/**
	 * Writes the carried attributes a message has, each as JSON.
	 *
	 * @param message       the message
	 * @param serialization writes the values
	 * @return the values by full key
	 */
	@NotNull Map<String, String> write(@NotNull ChatMessage message, @NotNull SerializationService serialization) {
		Map<String, String> written = new HashMap<>();
		for (AttributeKey<?> key : keys.values())
			message.getAttribute(key).ifPresent(value -> written.put(
					key.getFullKey(),
					new String(serialization.serialize(value), StandardCharsets.UTF_8)
			));

		return written;
	}

	/**
	 * Sets on a message the attributes another node wrote that this node carries too; a value that cannot be read is
	 * left behind and logged.
	 *
	 * @param written       the values by full key
	 * @param message       the message
	 * @param serialization reads the values
	 */
	void read(@NotNull Map<String, String> written, @NotNull ChatMessage message, @NotNull SerializationService serialization) {
		written.forEach((fullKey, json) -> {
			AttributeKey<?> key = keys.get(fullKey);
			if (key == null) return;

			try {
				set(message, key, serialization.deserialize(json.getBytes(StandardCharsets.UTF_8), key.getType()));
			} catch (RuntimeException failure) {
				Logger.warn("Leaving the attribute %s of a relayed message behind: %s", fullKey, failure.getMessage());
			}
		});
	}

	private static <T> void set(ChatMessage message, AttributeKey<T> key, Object value) {
		message.setAttribute(key, key.getType().cast(value));
	}
}
