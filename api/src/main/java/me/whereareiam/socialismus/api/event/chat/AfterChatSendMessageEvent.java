package me.whereareiam.socialismus.api.event.chat;

import me.whereareiam.socialismus.api.model.ChatMessage;

/**
 * This event is called after a message has been sent to the chat.
 * <p>
 * Allows you to take any action after a message has been sent to the chat.
 *
 * @since 1.0.0
 */
public class AfterChatSendMessageEvent extends ChatEvent {
	/**
	 * Constructor for the event.
	 *
	 * @param chatMessage The ChatMessage object that has been sent to the chat.
	 * @since 1.0.0
	 */
	public AfterChatSendMessageEvent(ChatMessage chatMessage) {
		super(chatMessage);
	}
}
