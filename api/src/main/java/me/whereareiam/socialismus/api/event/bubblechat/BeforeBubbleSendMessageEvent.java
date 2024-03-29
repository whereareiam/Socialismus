package me.whereareiam.socialismus.api.event.bubblechat;

import me.whereareiam.socialismus.api.model.BubbleMessage;
import org.bukkit.event.Cancellable;

/**
 * This event is called before a message is sent to the bubble chat.
 *
 * @since 1.2.0
 */
public class BeforeBubbleSendMessageEvent extends BubbleEvent implements Cancellable {
	private boolean cancelled;

	/**
	 * Constructor for the event.
	 *
	 * @param bubbleMessage The BubbleMessage object that is about to be sent to the bubble chat.
	 * @since 1.2.0
	 */
	public BeforeBubbleSendMessageEvent(BubbleMessage bubbleMessage) {
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
