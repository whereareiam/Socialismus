package me.whereareiam.socialismus.output.listener;

/**
 * A generic interface for handling events with dynamic priorities in the Socialismus plugin.
 * Unlike traditional Minecraft event listeners that use static {@code @EventHandler} priorities,
 * this interface allows for runtime modification of event handling priorities.
 *
 * This approach provides more flexibility than the standard Minecraft event system,
 * allowing listeners to adapt their priority based on runtime conditions.
 *
 * @param <T> the type of event this listener will handle
 */
public interface DynamicListener<T> {
    /**
     * Handles the occurrence of an event with dynamic priority.
     * The implementation can determine its priority at runtime based on
     * current conditions or configuration.
     *
     * @param event the event instance to be processed
     */
    void onEvent(T event);
}