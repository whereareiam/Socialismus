package me.whereareiam.socialismus.api.model.chatmention.mention;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collection;

public class Mention {
	private Component content;
	private boolean mentionAllTag;
	private boolean mentionNearbyTag;

	private Player sender;
	private Collection<? extends Player> mentionedPlayers;

	public Mention(Component content, boolean mentionAllTag, boolean mentionNearbyTag, Player sender,
	               Collection<? extends Player> mentionedPlayers) {
		this.content = content;
		this.mentionAllTag = mentionAllTag;
		this.mentionNearbyTag = mentionNearbyTag;
		this.sender = sender;
		this.mentionedPlayers = mentionedPlayers;
	}

	public Component getContent() {
		return content;
	}

	public void setContent(Component content) {
		this.content = content;
	}

	public boolean canMentionAll() {
		return mentionAllTag;
	}

	public void setMentionAllTag(boolean mentionAllTag) {
		this.mentionAllTag = mentionAllTag;
	}

	public boolean canMentionNearby() {
		return mentionNearbyTag;
	}

	public void setMentionNearbyTag(boolean mentionNearbyTag) {
		this.mentionNearbyTag = mentionNearbyTag;
	}

	public Player getSender() {
		return sender;
	}

	public void setSender(Player sender) {
		this.sender = sender;
	}

	public Collection<? extends Player> getMentionedPlayers() {
		return mentionedPlayers;
	}

	public void setMentionedPlayers(Collection<? extends Player> mentionedPlayers) {
		this.mentionedPlayers = mentionedPlayers;
	}
}
