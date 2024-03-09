package me.whereareiam.socialismus.api.model.chat;

import me.whereareiam.socialismus.api.type.ChatUseType;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ChatMessage {
	private final Player sender;
	private final ChatUseType useType;
	private Component content;
	private Chat chat;
	private Collection<? extends Player> recipients;
	private boolean cancelled;

	public ChatMessage(Player sender, ChatUseType useType, Component content, Chat chat,
	                   Collection<? extends Player> recipients, boolean cancelled) {
		this.sender = sender;
		this.useType = useType;
		this.content = content;
		this.chat = chat;
		this.recipients = recipients;
		this.cancelled = cancelled;
	}

	public Player getSender() {
		return sender;
	}

	public Component getContent() {
		return content;
	}

	public void setContent(Component content) {
		this.content = content;
	}

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public Collection<? extends Player> getRecipients() {
		return recipients;
	}

	public void setRecipients(Collection<? extends Player> recipients) {
		this.recipients = recipients;
	}

	public ChatUseType getUseType() {
		return useType;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
