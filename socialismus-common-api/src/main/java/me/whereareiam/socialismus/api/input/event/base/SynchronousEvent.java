package me.whereareiam.socialismus.api.input.event.base;

/**
 * Marker interface for events that must be executed synchronously.
 * <p>
 * When an event implements this interface, all of its handlers will be
 * executed on the calling thread instead of being dispatched to the
 * asynchronous executor used for normal events.
 */
public interface SynchronousEvent extends Event {
}
