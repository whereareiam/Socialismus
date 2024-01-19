package me.whereareiam.socialismus.api.event.bubblechat;

import me.whereareiam.socialismus.api.model.BubbleMessage;
import org.bukkit.event.Cancellable;

/**
 * This event is called when a message is about to be sent to the bubble chat.
 * <p>
 * Allows to modify the BubbleMessage object before it is sent to the bubble chat, by
 * changing the message, recipients, display time or sender.
 *
 * @since 1.2.0
 */
public class OnBubbleSendMessageEvent extends BubbleEvent implements Cancellable {
	private boolean cancelled;

	public OnBubbleSendMessageEvent(BubbleMessage bubbleMessage) {
		super(bubbleMessage);
	}

	/**
	 * Check if the event is cancelled.
	 *
	 * @return true if the event is cancelled, false otherwise.
	 * @since 1.2.0
	 */
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Set the event to cancelled or not.
	 * <p>
	 * If the event is cancelled, the message will not be sent to the bubble chat
	 * and other modules won't do any further processing on the message.
	 *
	 * @param cancelled true if the event should be cancelled, false otherwise.
	 * @since 1.2.0
	 */
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
