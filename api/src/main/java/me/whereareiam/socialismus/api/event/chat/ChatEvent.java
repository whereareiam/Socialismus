package me.whereareiam.socialismus.api.event.chat;

import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class ChatEvent extends Event {
	private static final HandlerList HANDLER_LIST = new HandlerList();
	protected ChatMessage chatMessage;
	protected Collection<? extends Player> recipients;

	public ChatEvent(ChatMessage chatMessage, Collection<? extends Player> recipients) {
		this.chatMessage = chatMessage;
		this.recipients = recipients;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public ChatMessage getChatMessage() {
		return chatMessage;
	}

	public void setChatMessage(ChatMessage chatMessage) {
		this.chatMessage = chatMessage;
	}

	public Collection<? extends Player> getRecipients() {
		return recipients;
	}

	public void setRecipients(Collection<? extends Player> recipients) {
		this.recipients = recipients;
	}

	public void removeRecipient(Player player) {
		recipients.remove(player);
	}
}