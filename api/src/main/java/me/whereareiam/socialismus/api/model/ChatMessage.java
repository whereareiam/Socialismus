package me.whereareiam.socialismus.api.model;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ChatMessage {
	private Component content;
	private Chat chat;
	private Player sender;
	private Collection<? extends Player> recipients;
	private boolean cancelled;

	public ChatMessage(Component content, Chat chat, Player sender, Collection<? extends Player> recipients, boolean cancelled) {
		this.content = content;
		this.chat = chat;
		this.sender = sender;
		this.recipients = recipients;
		this.cancelled = cancelled;
	}

	public Player getSender() {
		return sender;
	}

	public void setSender(Player sender) {
		this.sender = sender;
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

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
