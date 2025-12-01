package me.whereareiam.socialismus.model.serializer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Represents a placeholder and its corresponding value in the serialization process.
 * Used by {@link SerializerContent} for replacing placeholders in messages during formatting.
 *
 * <p>A placeholder consists of:</p>
 * <ul>
 *   <li>The placeholder text to be replaced (e.g., "%player_name%")</li>
 *   <li>The actual value to replace it with (e.g., "Bananas")</li>
 * </ul>
 */
@Getter
@ToString
@AllArgsConstructor
public class SerializerPlaceholder {
		/** The placeholder text to be replaced */
		private final String placeholder;

		/** The value that will replace the placeholder */
		private final String value;
}