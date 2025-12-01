package me.whereareiam.socialismus.input.event.base;

/**
 * Interface for events that can be cancelled.
 * Implementing classes can use this to control whether an event should proceed or not.
 */
public interface CancellableEvent {
    /**
     * Checks if the event is cancelled.
     *
     * @return true if the event is cancelled, false otherwise
     */
    boolean isCancelled();

    /**
     * Sets the cancelled state of the event.
     *
     * @param cancelled true to cancel the event, false to allow it
     */
    void setCancelled(boolean cancelled);
}