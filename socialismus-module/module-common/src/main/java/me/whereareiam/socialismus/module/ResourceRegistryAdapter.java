package me.whereareiam.socialismus.module;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.resource.ResourceRegisteredEvent;
import me.whereareiam.socialismus.event.resource.ResourceUnregisteredEvent;
import me.whereareiam.socialismus.model.resource.ResourceKey;
import me.whereareiam.socialismus.registry.ResourceRegistry;
import me.whereareiam.socialismus.type.ResourceRegistrationOutcome;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps one resource per full key, so that keys of the same name but another type cannot hold a second resource,
 * and announces each change once it is made.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ResourceRegistryAdapter implements ResourceRegistry {
	private final EventManager eventManager;
	private final Map<String, Registration> resources = new ConcurrentHashMap<>();

	@Override
	public @NotNull <T> ResourceRegistrationOutcome register(@NotNull ResourceKey<T> key, @NotNull T resource) {
		Registration registration = new Registration(key, key.cast(resource));
		if (resources.putIfAbsent(key.getFullKey(), registration) != null) return ResourceRegistrationOutcome.KEY_TAKEN;

		eventManager.call(new ResourceRegisteredEvent(key, resource));

		return ResourceRegistrationOutcome.REGISTERED;
	}

	@Override
	public @NotNull <T> Optional<T> get(@NotNull ResourceKey<T> key) {
		Registration registration = resources.get(key.getFullKey());
		if (registration == null) return Optional.empty();

		return Optional.of(key.cast(registration.resource()));
	}

	@Override
	public boolean has(@NotNull ResourceKey<?> key) {
		return has(key.getFullKey());
	}

	@Override
	public boolean has(@NotNull String fullKey) {
		return resources.containsKey(fullKey);
	}

	@Override
	public <T> boolean unregister(@NotNull ResourceKey<T> key, @NotNull T resource) {
		Registration registration = resources.get(key.getFullKey());
		if (registration == null || registration.resource() != resource) return false;
		if (!resources.remove(key.getFullKey(), registration)) return false;

		eventManager.call(new ResourceUnregisteredEvent(registration.key(), resource));

		return true;
	}

	private record Registration(ResourceKey<?> key, Object resource) {
	}
}
