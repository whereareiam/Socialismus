package me.whereareiam.socialismus.api.model.chat.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import net.kyori.adventure.text.Component;

/**
 * Represents a chat message with applied formatting.
 * Extends {@link ChatMessage} to add Adventure's Component-based formatting capabilities.
 * Used in the chat system to store and manage formatted message content.
 *
 * The format is stored as a Kyori Adventure Component which supports rich text
 * formatting including colors, styles, and click/hover events.
 */
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
public class FormattedChatMessage extends ChatMessage {
    /**
     * The formatted content of the message as an Adventure Component
     */
    private Component format;
}