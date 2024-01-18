package me.whereareiam.socialismus.api.model;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collection;

public class BubbleMessage {
	private double displayTime;
	private Component content;
	private Player sender;
	private Collection<? extends Player> receivers;

	public BubbleMessage(double displayTime, Component content, Player sender,
						 Collection<? extends Player> receivers) {
		this.displayTime = displayTime;
		this.content = content;
		this.sender = sender;
		this.receivers = receivers;
	}

	public double getDisplayTime() {
		return displayTime;
	}

	public void setDisplayTime(double displayTime) {
		this.displayTime = displayTime;
	}

	public Component getContent() {
		return content;
	}

	public void setContent(Component content) {
		this.content = content;
	}

	public Player getSender() {
		return sender;
	}

	public void setSender(Player sender) {
		this.sender = sender;
	}

	public Collection<? extends Player> getReceivers() {
		return receivers;
	}

	public void setReceivers(Collection<? extends Player> receivers) {
		this.receivers = receivers;
	}
}
