package me.whereareiam.socialismus.module.network.api.transport;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

/**
 * Keeps the entries of replicated caches where every node reads them: what a transport that can store, such as
 * SocialismusRedis, registers under {@link me.whereareiam.socialismus.module.network.api.NetworkResources#STORE}.
 * With a store, a starting node loads the caches from it and conditional writes are atomic for every node.
 *
 * <p>Entries are opaque bytes under a namespace, one per cache, and a key. Calls may block; the network never makes
 * them on a server's main thread.</p>
 */
public interface ReplicationStore {
	/**
	 * Reads an entry.
	 *
	 * @param namespace the cache's namespace
	 * @param key       the entry's key
	 * @return the stored bytes, or empty when there is no entry or it expired
	 */
	@NotNull Optional<byte[]> get(@NotNull String namespace, @NotNull String key);

	/**
	 * Reads every entry of a namespace that has not expired.
	 *
	 * @param namespace the cache's namespace
	 * @return the stored bytes by key
	 */
	@NotNull Map<String, byte[]> entries(@NotNull String namespace);

	/**
	 * Stores an entry only while the stored one is exactly as expected, in one step for every node.
	 *
	 * @param namespace the cache's namespace
	 * @param key       the entry's key
	 * @param expected  the bytes that must be stored, or {@code null} when nothing may be stored under the key
	 * @param value     the bytes to store
	 * @param ttl       how long the entry lives, or {@code null} for no expiry
	 * @return {@code true} when stored, {@code false} when the stored entry was different
	 */
	boolean compareAndPut(
			@NotNull String namespace,
			@NotNull String key,
			byte @Nullable [] expected,
			byte @NotNull [] value,
			@Nullable Duration ttl
	);

	/**
	 * Removes an entry only while the stored one is exactly as expected, in one step for every node.
	 *
	 * @param namespace the cache's namespace
	 * @param key       the entry's key
	 * @param expected  the bytes that must be stored
	 * @return {@code true} when removed, {@code false} when the stored entry was different or gone
	 */
	boolean compareAndRemove(@NotNull String namespace, @NotNull String key, byte @NotNull [] expected);
}
