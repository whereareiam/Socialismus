package me.whereareiam.socialismus.api.event.chat;

import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * This event is called after a message has been sent to the chat.
 * <p>
 * Allows you to take any action after a message has been sent to the chat.
 *
 * @since 1.0.0
 */
public class AfterChatSendMessageEvent extends ChatEvent {
	public AfterChatSendMessageEvent(ChatMessage chatMessage, Collection<? extends Player> recipients) {
		super(chatMessage, recipients);
	}
}
