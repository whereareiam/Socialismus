package me.whereareiam.socialismus.common;

import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.model.config.Settings;
import me.whereareiam.socialismus.api.output.listener.ListenerRegistrar;
import me.whereareiam.socialismus.api.type.EventPriority;

@RequiredArgsConstructor
public abstract class CommonListenerRegistrar implements ListenerRegistrar {
	protected final Provider<Settings> settings;

	protected EventPriority determinePriority(Class<?> event) {
		Settings.Listeners listeners = settings.get().getListeners();

		if (listeners == null || listeners.getEvents() == null
				|| listeners.getEvents().isEmpty() || listeners.getEvents().get(event.getName()) == null)
			return EventPriority.NORMAL;

		EventPriority priority = listeners.getEvents().get(event.getName()).getPriority();
		if (priority == null) {
			Logger.warn("No priority found for event " + event.getName() + ", using default NORMAL.");
			return EventPriority.NORMAL;
		}

		return priority;
	}
}
