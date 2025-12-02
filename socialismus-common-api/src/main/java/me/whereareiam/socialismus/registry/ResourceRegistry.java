package me.whereareiam.socialismus.registry;

import me.whereareiam.socialismus.type.ResourceType;

import java.util.Optional;

/**
 * Central registry of shared resources (DB, cache, sync, …)
 * Providers register implementations here; consumers look them up.
 */
public interface ResourceRegistry {
	/**
	 * Register an implementation for a resource type (first wins).
	 */
	<T> void register(ResourceType type, T impl);

	/**
	 * Look up the current implementation, if any.
	 */
	<T> Optional<T> get(ResourceType type);

	/**
	 * True if a provider has been registered for this type.
	 */
	boolean has(ResourceType type);

	/**
	 * Unregister the given resource type.
	 */
	void unregister(ResourceType type);
}
