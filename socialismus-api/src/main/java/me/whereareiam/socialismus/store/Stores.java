package me.whereareiam.socialismus.store;

import org.jetbrains.annotations.NotNull;

/**
 * Hands features their {@link Store}s, and takes what modules offer to keep them: {@link StoreReplication} to share
 * values between servers and {@link StorePersistence} to keep them across restarts. Without either, a store lives in
 * this server's memory; when one is offered or withdrawn, every store whose key asks for it moves its values over.
 *
 * <pre>{@code
 * Store<UUID, Mode> modes = stores.of(StoreKey.of("mentionator:modes", UUID.class, Mode.class).replicated().persistent());
 *
 * // a module that shares values between servers
 * stores.addReplication(replication);
 * stores.removeReplication(replication);
 * }</pre>
 */
public interface Stores {
	/**
	 * Returns the store of a key. Asking twice for one name returns the same store.
	 *
	 * @param key the store's key
	 * @param <K> key type
	 * @param <V> value type
	 * @return the store
	 * @throws IllegalArgumentException when a store of that name was obtained with another key
	 */
	<K, V> @NotNull Store<K, V> of(@NotNull StoreKey<K, V> key);

	/**
	 * Shares the values of replicated stores through a replication, in place of the one offered before.
	 *
	 * @param replication the replication
	 */
	void addReplication(@NotNull StoreReplication replication);

	/**
	 * Withdraws a replication; its stores keep their values in this server's memory.
	 *
	 * @param replication the replication offered before
	 */
	void removeReplication(@NotNull StoreReplication replication);

	/**
	 * Keeps the values of persistent stores through a persistence, in place of the one offered before. Each such
	 * store loads what it holds there, keeping the values it has for the same keys.
	 *
	 * @param persistence the persistence
	 */
	void addPersistence(@NotNull StorePersistence persistence);

	/**
	 * Withdraws a persistence; its stores keep their values, without saving them anymore.
	 *
	 * @param persistence the persistence offered before
	 */
	void removePersistence(@NotNull StorePersistence persistence);
}
