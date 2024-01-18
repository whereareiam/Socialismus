package me.whereareiam.socialismus.api.event.bubblechat;

import me.whereareiam.socialismus.api.model.BubbleMessage;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class BubbleEvent extends Event {
	private static final HandlerList HANDLER_LIST = new HandlerList();
	protected BubbleMessage bubbleMessage;

	public BubbleEvent(BubbleMessage bubbleMessage) {
		this.bubbleMessage = bubbleMessage;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public BubbleMessage getBubbleMessage() {
		return bubbleMessage;
	}

	public void setBubbleMessage(BubbleMessage bubbleMessage) {
		this.bubbleMessage = bubbleMessage;
	}
}
