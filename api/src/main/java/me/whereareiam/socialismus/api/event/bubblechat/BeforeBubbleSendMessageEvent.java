package me.whereareiam.socialismus.api.event.bubblechat;

import me.whereareiam.socialismus.api.model.BubbleMessage;
import org.bukkit.event.Cancellable;

public class BeforeBubbleSendMessageEvent extends BubbleEvent implements Cancellable {
	private boolean cancelled;

	public BeforeBubbleSendMessageEvent(BubbleMessage bubbleMessage) {
		super(bubbleMessage);
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
