package me.whereareiam.socialismus.input.chat;

import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;

/**
 * Service interface that acts as an entry point for Minecraft chat event handling.
 * Coordinates the processing and formatting of chat messages triggered by Minecraft events.
 *
 * <p>This service handles the complete message processing pipeline, including:
 * <ul>
 *   <li>Initial chat event processing</li>
 *   <li>Message formatting and transformation</li>
 *   <li>Application of chat rules and filters</li>
 *   <li>Preparation for broadcasting</li>
 * </ul>
 */
public interface ChatCoordinationService {
    /**
     * Coordinates the processing of a Minecraft chat message through the entire pipeline.
     * Takes a raw chat message from a Minecraft event and processes it into a fully
     * formatted message ready for broadcasting.
     *
     * @param chatMessage the raw chat message from Minecraft event
     * @return the processed and formatted chat message
     */
    FormattedChatMessage coordinate(ChatMessage chatMessage);
}