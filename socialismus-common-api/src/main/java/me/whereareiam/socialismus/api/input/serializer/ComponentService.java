package me.whereareiam.socialismus.api.input.serializer;

import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.serializer.SerializerContent;
import net.kyori.adventure.text.Component;

/**
 * Interface for serializing and formatting text content into Adventure Components.
 * Provides methods to format messages with player context and serialize content objects.
 *
 * <p>This service handles:</p>
 * <ul>
 *   <li>Player-specific message formatting</li>
 *   <li>Generic content serialization</li>
 *   <li>Conversion of text to Adventure Components</li>
 * </ul>
 */
public interface ComponentService {
	/**
	 * Formats a message with player-specific context.
	 *
	 * @param dummyPlayer the player context for formatting
	 * @param message     the message to format
	 * @return the formatted message as an Adventure Component
	 */
	Component format(DummyPlayer dummyPlayer, String message);

	/**
	 * Formats serializer content into a component.
	 *
	 * @param content the content to format
	 * @return the formatted content as an Adventure Component
	 */
	Component format(SerializerContent content);
}