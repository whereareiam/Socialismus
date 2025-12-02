package me.whereareiam.socialismus.service.resource;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

public interface CacheService {
	/**
	 * Retrieve a value of the given type from the cache.
	 */
	<T> Optional<T> get(String key, Class<T> type);

	/**
	 * Store a value in the cache, never expiring.
	 */
	<T> void put(String key, T value);

	/**
	 * Store a value in the cache with a TTL.
	 */
	<T> void put(String key, T value, Duration ttl);

	/**
	 * Remove a key (and its value) from the cache.
	 *
	 * @return true if the key was present and removed.
	 */
	boolean delete(String key);

	/**
	 * Check whether a key exists in the cache.
	 */
	boolean exists(String key);

	/**
	 * Add a member to a named set.
	 *
	 * @return true if the member was newly added.
	 */
	boolean add(String key, String member);

	/**
	 * Remove a member from a named set.
	 *
	 * @return true if the member was present and removed.
	 */
	boolean remove(String key, String member);

	/**
	 * List all members of a named set.
	 */
	Set<String> get(String key);
}
