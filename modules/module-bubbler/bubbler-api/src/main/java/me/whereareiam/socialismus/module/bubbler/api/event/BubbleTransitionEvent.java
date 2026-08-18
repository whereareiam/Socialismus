package me.whereareiam.socialismus.module.bubbler.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.whereareiam.socialismus.event.base.Event;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;
import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleTransition;
import me.whereareiam.socialismus.module.bubbler.api.type.TransitionType;

/**
 * Event triggered when a bubble transition occurs.
 * Transitions include BEFORE (before bubble appears), INTER (between groups), and AFTER (after bubble disappears).
 */
@Getter
@AllArgsConstructor
public class BubbleTransitionEvent implements Event {
	private final TransitionType transitionType;
	private final BubbleMessage bubbleMessage;

	/**
	 * Convenience method to get the transition for this event.
	 * @return the bubble transition for this transition type, or null if not configured
	 */
	public BubbleTransition getTransition() {
		return bubbleMessage.getBubble().getTransitions().get(transitionType);
	}
}
