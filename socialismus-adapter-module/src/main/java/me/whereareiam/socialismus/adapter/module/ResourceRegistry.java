package me.whereareiam.socialismus.adapter.module;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.type.ResourceType;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ResourceRegistry {
	private final Map<ResourceType, Object> resources = new ConcurrentHashMap<>();

	/**
	 * Register the given implementation for this type;
	 * if a provider is already registered, ignore the new one.
	 */
	public <T> void register(ResourceType type, T impl) {
		resources.computeIfAbsent(type, key -> {
			Logger.info("Registered resource " + type + " → " + impl.getClass().getSimpleName());
			return impl;
		});
	}

	@SuppressWarnings("unchecked")
	public <T> Optional<T> get(ResourceType type) {
		return Optional.ofNullable((T) resources.get(type));
	}

	public boolean has(ResourceType type) {
		return resources.containsKey(type);
	}

	public void unregister(ResourceType type) {
		resources.remove(type);
	}
}
