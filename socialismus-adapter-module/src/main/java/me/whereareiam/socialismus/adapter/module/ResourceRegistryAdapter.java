package me.whereareiam.socialismus.adapter.module;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.registry.ResourceRegistry;
import me.whereareiam.socialismus.type.ResourceType;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ResourceRegistryAdapter implements ResourceRegistry {
	private final Map<ResourceType, Object> resources = new ConcurrentHashMap<>();

	@Override
	public <T> void register(ResourceType type, T impl) {
		resources.computeIfAbsent(type, key -> {
			Logger.info("Registered resource " + type + " → " + impl.getClass().getSimpleName());
			return impl;
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Optional<T> get(ResourceType type) {
		return Optional.ofNullable((T) resources.get(type));
	}

	@Override
	public boolean has(ResourceType type) {
		return resources.containsKey(type);
	}

	@Override
	public void unregister(ResourceType type) {
		resources.remove(type);
	}
}
