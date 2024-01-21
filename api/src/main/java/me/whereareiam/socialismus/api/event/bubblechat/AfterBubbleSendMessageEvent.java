package me.whereareiam.socialismus.api.event.bubblechat;

import me.whereareiam.socialismus.api.model.BubbleMessage;

/**
 * This event is called after a message has been sent to the bubble chat.
 * <p>
 * Allows you to take any action after a message has been sent to the bubble chat.
 *
 * @since 1.2.0
 */
public class AfterBubbleSendMessageEvent extends BubbleEvent {
	/**
	 * Constructor for the event.
	 *
	 * @param bubbleMessage The BubbleMessage object that has been sent to the bubble chat.
	 * @since 1.2.0
	 */
	public AfterBubbleSendMessageEvent(BubbleMessage bubbleMessage) {
		super(bubbleMessage);
	}
}
