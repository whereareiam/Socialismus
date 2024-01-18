package me.whereareiam.socialismus.api.event.chat;

import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import org.bukkit.entity.Player;

import java.util.Collection;

public class AfterChatSendMessageEvent extends ChatEvent {
	public AfterChatSendMessageEvent(ChatMessage chatMessage, Collection<? extends Player> recipients) {
		super(chatMessage, recipients);
	}
}
