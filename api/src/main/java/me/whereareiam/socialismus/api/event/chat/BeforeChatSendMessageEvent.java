package me.whereareiam.socialismus.api.event.chat;

import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.Collection;

public class BeforeChatSendMessageEvent extends ChatEvent implements Cancellable {
	private boolean cancelled;

	public BeforeChatSendMessageEvent(ChatMessage chatMessage, Collection<? extends Player> recipients) {
		super(chatMessage, recipients);
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
