package me.whereareiam.socialismus.api.event.bubblechat;

import me.whereareiam.socialismus.api.model.BubbleMessage;

public class AfterBubbleSendMessageEvent extends BubbleEvent {
	public AfterBubbleSendMessageEvent(BubbleMessage bubbleMessage) {
		super(bubbleMessage);
	}
}
