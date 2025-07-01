package me.whereareiam.socialismus.common.event;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.input.event.EventListener;
import me.whereareiam.socialismus.api.input.event.EventManager;
import me.whereareiam.socialismus.api.input.event.base.CancellableEvent;
import me.whereareiam.socialismus.api.input.event.base.Event;
import me.whereareiam.socialismus.api.input.event.base.EventOrder;
import me.whereareiam.socialismus.api.input.event.base.SocialisticEvent;

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
		if (clazz == null || !Event.class.isAssignableFrom(clazz)) {
			return;
		}

		types.add(clazz);
		collectEventTypes(clazz.getSuperclass(), types);

		for (Class<?> iface : clazz.getInterfaces()) {
			collectEventTypes(iface, types);
		}
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

		for (RegisteredListener listener : eventListeners) {
			executor.submit(() -> {
				try {
					listener.getMethod().invoke(listener.getListener(), event);
				} catch (Exception e) {
					Logger.severe("Failed to call event " + event.getClass().getSimpleName() + " for listener " + listener.getListener().getClass().getSimpleName());
					e.printStackTrace();
				}
			});

			if (event instanceof CancellableEvent && ((CancellableEvent) event).isCancelled()) {
				Logger.debug("Event " + event.getClass().getSimpleName() + " was cancelled");
				break;
			}
		}
	}
}
