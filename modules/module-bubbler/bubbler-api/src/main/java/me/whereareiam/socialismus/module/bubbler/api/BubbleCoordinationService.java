package me.whereareiam.socialismus.module.bubbler.api;

import me.whereareiam.socialismus.module.bubbler.api.model.bubble.BubbleMessage;

/**
 * Service responsible for coordinating the display and lifecycle of chat bubbles.
 * Handles the orchestration of bubble rendering, animation, and removal for players.
 */
public interface BubbleCoordinationService {
    /**
     * Coordinates the display of a bubble message to recipients.
     * This includes spawning the bubble, managing its animation, and scheduling removal.
     *
     * @param bubbleMessage the bubble message containing sender, recipients, and display configuration
     */
    void coordinate(BubbleMessage bubbleMessage);
}
