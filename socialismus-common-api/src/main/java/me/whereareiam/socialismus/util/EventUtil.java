package me.whereareiam.socialismus.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.input.event.EventManager;
import me.whereareiam.socialismus.input.event.base.CancellableEvent;
import me.whereareiam.socialismus.input.event.base.Event;

/**
 * Utility class for handling events in the Socialismus plugin.
 * Provides a convenient way to call events and handle their cancellation state.
 * Uses dependency injection to manage the event system components.
 * <p>
 * This class acts as a bridge between the event system and the plugin's logic,
 * allowing events to be called with associated callbacks that execute only if
 * the event is not cancelled.
 */
@Singleton
public class EventUtil {
	private static EventManager eventManager;

	/**
	 * Constructs the EventUtil with the required EventManager.
	 *
	 * @param eventManager the event manager instance to use
	 */
	@Inject
	public EventUtil(EventManager eventManager) {
		EventUtil.eventManager = eventManager;
	}

	/**
	 * Calls an event and executes a callback if the event is not cancelled.
	 *
	 * @param event    the event to call
	 * @param callback the code to execute if the event is not cancelled
	 * @return true if the callback was executed, false if the event was cancelled
	 */
	public static boolean callEvent(Event event, Runnable callback) {
		EventUtil.eventManager.call(event);

		if (event instanceof CancellableEvent cancellableEvent && cancellableEvent.isCancelled())
			return false;

		callback.run();
		return true;
	}
}