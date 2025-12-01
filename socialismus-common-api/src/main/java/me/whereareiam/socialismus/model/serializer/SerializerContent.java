package me.whereareiam.socialismus.model.serializer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.model.player.DummyPlayer;

import java.util.List;

/**
 * Represents the content to be serialized in the serialization process.
 * Contains all necessary information for message formatting and placeholder resolution.
 *
 * <p>This class holds:</p>
 * <ul>
 *   <li>Player context for player-specific formatting</li>
 *   <li>A list of placeholders to be replaced in the message</li>
 *   <li>The actual message text to be processed</li>
 * </ul>
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class SerializerContent {
		/** The player context for message formatting */
		private final DummyPlayer dummyPlayer;

		/** List of placeholders to be processed during serialization */
		private final List<SerializerPlaceholder> placeholders;

		/** The message text to be formatted */
		private String message;
}