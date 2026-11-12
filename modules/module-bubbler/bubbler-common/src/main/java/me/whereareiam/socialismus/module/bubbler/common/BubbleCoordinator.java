package me.whereareiam.socialismus.module.bubbler.common;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.bubbler.api.BubbleCoordinationService;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.common.animation.BubbleAnimationFactory;
import me.whereareiam.socialismus.module.bubbler.common.processor.BubbleMessageProcessor;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class BubbleCoordinator implements BubbleCoordinationService {
	private final BubbleMessageProcessor bubbleMessageProcessor;
	private final BubbleAnimationFactory bubbleAnimationFactory;

	@Override
	public void coordinate(BubbleMessage bubbleMessage) {
		Logger.debug("Coordinating bubble message for: " + bubbleMessage.getSender().getUsername());
		bubbleMessage = bubbleMessageProcessor.process(bubbleMessage);
		if (bubbleMessage.isCancelled()) return;

		Logger.debug("Animating bubble message for: " + bubbleMessage.getSender().getUsername() + " with animation: "
				+ bubbleMessage.getBubble().getStyle().getAnimation());
		bubbleAnimationFactory
				.getAnimation(bubbleMessage.getBubble().getStyle().getAnimation())
				.display(bubbleMessage);
	}
}
