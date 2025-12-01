package me.whereareiam.socialismus.common.event;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.input.event.EventListener;
import me.whereareiam.socialismus.api.input.event.EventManager;
import me.whereareiam.socialismus.api.input.event.base.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class EventController implements EventManager {
	private final Map<Class<?>, List<RegisteredListener>> listeners = new HashMap<>();
	private final ExecutorService executor = Executors.newCachedThreadPool();

	@Override
	public void register(EventListener listener) {
		for (Method method : listener.getClass().getDeclaredMethods()) {
			if (method.isAnnotationPresent(SocialisticEvent.class)) {
				Class<?> eventType = method.getParameterTypes()[0];
				EventOrder order = method.getAnnotation(SocialisticEvent.class).value();

				listeners.computeIfAbsent(eventType, k -> new ArrayList<>())
						.add(new RegisteredListener(listener, method, order));
				listeners.get(eventType).sort(Comparator.comparing(RegisteredListener::getOrder));
			}
		}
	}

	@Override
	public <T extends Event> void registerListener(Class<T> event, Object listener, Method method, EventOrder order) {
		listeners.computeIfAbsent(event, k -> new ArrayList<>())
				.add(new RegisteredListener((EventListener) listener, method, order));
		listeners.get(event).sort(Comparator.comparing(RegisteredListener::getOrder));
	}

	@Override
	public void unregister(EventListener eventListener) {
		listeners.values().forEach(list -> list.removeIf(listener -> listener.getListener().equals(eventListener)));
	}

	private void collectEventTypes(Class<?> clazz, Set<Class<?>> types) {
		if (clazz == null || !Event.class.isAssignableFrom(clazz))
			return;

		types.add(clazz);
		collectEventTypes(clazz.getSuperclass(), types);

		for (Class<?> iface : clazz.getInterfaces())
			collectEventTypes(iface, types);
	}

	@Override
	public void call(Event event) {
		Set<Class<?>> eventTypes = new HashSet<>();
		collectEventTypes(event.getClass(), eventTypes);

		List<RegisteredListener> eventListeners = eventTypes.stream()
				.flatMap(type -> listeners.getOrDefault(type, Collections.emptyList()).stream())
				.sorted(Comparator.comparing(RegisteredListener::getOrder))
				.toList();

		if (eventListeners.isEmpty()) return;

		boolean synchronous = event instanceof SynchronousEvent;

		for (RegisteredListener listener : eventListeners) {
			if (synchronous) {
				executeSynchronously(listener, event);
			} else {
				executeAsynchronously(listener, event);
			}

			if (isCancelled(event)) {
				break;
			}
		}
	}

	/**
	 * Executes an event listener synchronously on the current thread.
	 *
	 * @param listener The listener to execute
	 * @param event    The event to pass to the listener
	 */
	private void executeSynchronously(RegisteredListener listener, Event event) {
		try {
			listener.getMethod().invoke(listener.getListener(), event);
		} catch (Exception e) {
			logExecutionError(event, listener, e);
		}
	}

	/**
	 * Executes an event listener asynchronously on a separate thread.
	 *
	 * @param listener The listener to execute
	 * @param event    The event to pass to the listener
	 */
	private void executeAsynchronously(RegisteredListener listener, Event event) {
		executor.submit(() -> {
			try {
				listener.getMethod().invoke(listener.getListener(), event);
			} catch (Exception e) {
				logExecutionError(event, listener, e);
			}
		});
	}

	/**
	 * Checks if the event is cancelled.
	 *
	 * @param event The event to check
	 * @return true if the event is cancelled, false otherwise
	 */
	private boolean isCancelled(Event event) {
		if (!(event instanceof CancellableEvent cancellableEvent))
			return false;

		if (cancellableEvent.isCancelled()) {
			Logger.debug("Event " + event.getClass().getSimpleName() + " was cancelled");
			return true;
		}

		return false;
	}

	/**
	 * Logs an error that occurred during event execution.
	 *
	 * @param event    The event that was being processed
	 * @param listener The listener that failed
	 * @param e        The exception that occurred
	 */
	private void logExecutionError(Event event, RegisteredListener listener, Exception e) {
		Logger.severe("Failed to call event " + event.getClass().getSimpleName() +
				" for listener " + listener.getListener().getClass().getSimpleName());
		e.printStackTrace();
	}
}