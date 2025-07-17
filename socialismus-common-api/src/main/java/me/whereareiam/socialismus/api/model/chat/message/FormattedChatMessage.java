package me.whereareiam.socialismus.api.model.chat.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import net.kyori.adventure.text.Component;

/**
 * Represents a chat message with applied formatting.
 * Extends {@link ChatMessage} to add Adventure's Component-based formatting capabilities.
 * Used in the chat system to store and manage formatted message content.
 * <p>
 * The format is stored as a Kyori Adventure Component which supports rich text
 * formatting including colors, styles, and click/hover events.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
public class FormattedChatMessage extends ChatMessage {
	/**
	 * The formatted content of the message as an Adventure Component
	 */
	private Component format;

	public static FormattedChatMessage from(ChatMessage msg, Component format, boolean vanillaSending) {
		return FormattedChatMessage.builder()
				.id(msg.getId())
				.sender(msg.getSender())
				.recipients(msg.getRecipients())
				.content(msg.getContent())
				.chat(msg.getChat())
				.cancelled(msg.isCancelled())
				.vanillaSending(vanillaSending)
				.origin(msg.getOrigin())
				.format(format)
				.build();
	}
}