package me.whereareiam.socialismus.registry;

import me.whereareiam.socialismus.model.resource.ResourceKey;
import me.whereareiam.socialismus.type.ResourceRegistrationOutcome;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Central registry of shared resources, such as the synchronization, the cache and the database. Providers
 * register a resource under its {@link ResourceKey}; consumers look it up by the same key:
 * <pre>{@code
 * Optional<SyncService> sync = registry.get(Constants.Resources.SYNC);
 * }</pre>
 *
 * <p>A key holds one resource at a time. Resources come and go while Socialismus runs, as the modules that
 * provide them load and unload, so a consumer that has to follow them listens to the
 * {@link me.whereareiam.socialismus.event.resource.ResourceRegisteredEvent} and
 * {@link me.whereareiam.socialismus.event.resource.ResourceUnregisteredEvent}.
 *
 * <p>A module usually provides its resources through
 * {@link me.whereareiam.socialismus.service.resource.ResourceProvider}, which registers them when it loads and
 * unregisters them when it unloads.
 */
public interface ResourceRegistry {
	/**
	 * Registers a resource under its key, unless another resource holds the key. A registered resource is
	 * announced with a {@link me.whereareiam.socialismus.event.resource.ResourceRegisteredEvent}, called on this
	 * thread before the method returns.
	 *
	 * @param key      the key to register the resource under
	 * @param resource the resource
	 * @param <T>      the type of the resource
	 * @return {@link ResourceRegistrationOutcome#REGISTERED}, or {@link ResourceRegistrationOutcome#KEY_TAKEN} when
	 * a resource is registered under the key already, even the same one
	 * @throws IllegalArgumentException when the resource is not of the key's type
	 */
	@NotNull
	<T> ResourceRegistrationOutcome register(@NotNull ResourceKey<T> key, @NotNull T resource);

	/**
	 * Looks up the resource registered under a key.
	 *
	 * @param key the key of the resource
	 * @param <T> the type of the resource
	 * @return the resource, or empty when none is registered under the key
	 * @throws IllegalArgumentException when the registered resource is not of the key's type, because the
	 *                                  module that registered it defines the resource with another type
	 */
	@NotNull
	<T> Optional<T> get(@NotNull ResourceKey<T> key);

	/**
	 * Tells whether a resource is registered under a key.
	 *
	 * @param key the key of the resource
	 * @return {@code true} when a resource is registered under the key's full key
	 */
	boolean has(@NotNull ResourceKey<?> key);

	/**
	 * Tells whether a resource is registered under a full key, as a {@code module.json} names it.
	 *
	 * @param fullKey the full key of the resource, such as {@code socialismus:sync}
	 * @return {@code true} when a resource is registered under the full key
	 */
	boolean has(@NotNull String fullKey);

	/**
	 * Unregisters a resource, when it is the one registered under its key. An unregistered resource is
	 * announced with a {@link me.whereareiam.socialismus.event.resource.ResourceUnregisteredEvent}, called on
	 * this thread before the method returns.
	 *
	 * @param key      the key the resource is registered under
	 * @param resource the resource, the same instance that was registered
	 * @param <T>      the type of the resource
	 * @return {@code true} when the resource was unregistered, {@code false} when another resource or none is
	 * registered under the key
	 */
	<T> boolean unregister(@NotNull ResourceKey<T> key, @NotNull T resource);
}
