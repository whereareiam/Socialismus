package me.whereareiam.socialismus.api.model.chatmention.mention;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collection;

public class Mention {
	private Component content;
	private String usedAllTag;
	private String usedNearbyTag;

	private Player sender;
	private Collection<? extends Player> mentionedPlayers;

	public Mention(Component content, String usedAllTag, String usedNearbyTag, Player sender,
	               Collection<? extends Player> mentionedPlayers) {
		this.content = content;
		this.usedAllTag = usedAllTag;
		this.usedNearbyTag = usedNearbyTag;
		this.sender = sender;
		this.mentionedPlayers = mentionedPlayers;
	}

	public Component getContent() {
		return content;
	}

	public void setContent(Component content) {
		this.content = content;
	}

	public String getUsedAllTag() {
		return usedAllTag;
	}

	public void setUsedAllTag(String usedAllTag) {
		this.usedAllTag = usedAllTag;
	}

	public String getUsedNearbyTag() {
		return usedNearbyTag;
	}

	public void setUsedNearbyTag(String usedNearbyTag) {
		this.usedNearbyTag = usedNearbyTag;
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
