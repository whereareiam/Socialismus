package me.whereareiam.socialismus.module.bubbler.api.model.bubble;

import me.whereareiam.socialismus.service.Scheduler;

/**
 * Abstract base class for bubble display animations.
 * Implementations define how bubbles are animated when appearing,
 * displaying content, and disappearing.
 */
public abstract class BubbleAnimation {
  protected final Scheduler scheduler;

  /**
   * Creates a new bubble animation with the given scheduler.
   *
   * @param scheduler the scheduler for timing animation tasks
   */
  protected BubbleAnimation(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  /**
   * Displays the bubble message with this animation style.
   * Implementations should handle spawning, animating, and cleanup.
   *
   * @param bubbleMessage the message to display as a bubble
   */
  public abstract void display(BubbleMessage bubbleMessage);
}
