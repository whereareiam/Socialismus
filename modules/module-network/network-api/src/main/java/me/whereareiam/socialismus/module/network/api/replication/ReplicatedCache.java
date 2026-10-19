package me.whereareiam.socialismus.module.network.api.replication;

import me.whereareiam.socialismus.module.network.api.model.Origin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Entries every node of the network shares. Each node keeps its own copy, so reads never wait for the network; a
 * change goes out to the others and the newest version of an entry wins everywhere, so a node that was away is
 * corrected when it comes back instead of bringing old state back.
 *
 * <p>When the transport also stores entries, such as SocialismusRedis, the store is the source of truth: a starting
 * node loads from it, and {@link #write} and {@link #remove(String, Versioned)} are atomic for every node. Without a
 * store they are checked against this node's copy only, and of two nodes that change one entry at the same moment
 * the newer version wins.</p>
 *
 * <pre>{@code
 * ReplicatedCache<Boolean> spies = network.replication().cache("dialogue:spies", Boolean.class);
 * spies.put(player.toString(), true);
 * boolean spying = spies.get(player.toString()).orElse(false);
 * }</pre>
 *
 * @param <T> value type
 */
public interface ReplicatedCache<T> {
	/**
	 * Returns this node's copy of an entry.
	 *
	 * @param key the entry's key
	 * @return the value, or empty when there is no entry or it expired
	 */
	@NotNull Optional<T> get(@NotNull String key);

	/**
	 * Returns this node's copy of every entry that has not expired.
	 */
	@NotNull Map<String, T> entries();

	/**
	 * Sets an entry for every node, kept until it is changed or removed.
	 *
	 * @param key   the entry's key
	 * @param value the value
	 * @return completion of the write, after this node's copy changed and the change went out
	 */
	default @NotNull CompletableFuture<Void> put(@NotNull String key, @NotNull T value) {
		return put(key, value, null);
	}

	/**
	 * Sets an entry for every node until it expires.
	 *
	 * @param key   the entry's key
	 * @param value the value
	 * @param ttl   how long the entry lives, or {@code null} until it is changed or removed
	 * @return completion of the write, after this node's copy changed and the change went out
	 */
	@NotNull CompletableFuture<Void> put(@NotNull String key, @NotNull T value, @Nullable Duration ttl);

	/**
	 * Removes an entry for every node.
	 *
	 * @param key the entry's key
	 * @return completion of the removal
	 */
	@NotNull CompletableFuture<Void> remove(@NotNull String key);

	/**
	 * Reads this node's copy of an entry together with its revision, for a conditional {@link #write} or
	 * {@link #remove(String, Versioned)}. With a store, the condition is checked against the store, so a copy that
	 * missed a change makes the write fail instead of overwriting it.
	 *
	 * @param key the entry's key
	 * @return the value with its revision, or empty when there is no entry
	 */
	@NotNull Optional<Versioned<T>> read(@NotNull String key);

	/**
	 * Sets an entry only while it is still as it was read.
	 *
	 * @param key   the entry's key
	 * @param read  what was read: the entry must still be at that revision, or, when {@code null}, there must be no
	 *              entry
	 * @param value the value
	 * @param ttl   how long the entry lives, or {@code null} until it is changed or removed
	 * @return {@code true} when written, {@code false} when the entry changed since it was read
	 */
	@NotNull CompletableFuture<Boolean> write(@NotNull String key, @Nullable Versioned<T> read, @NotNull T value, @Nullable Duration ttl);

	/**
	 * Removes an entry only while it is still as it was read.
	 *
	 * @param key  the entry's key
	 * @param read what was read
	 * @return {@code true} when removed, {@code false} when the entry changed or was removed since it was read
	 */
	@NotNull CompletableFuture<Boolean> remove(@NotNull String key, @NotNull Versioned<T> read);

	/**
	 * Calls a listener whenever this node's copy of an entry changes, by this node or by another.
	 *
	 * @param listener the listener
	 */
	void onChange(@NotNull ChangeListener<T> listener);

	/**
	 * A change to one entry of a cache.
	 *
	 * @param <T> value type
	 */
	@FunctionalInterface
	interface ChangeListener<T> {
		/**
		 * Called after this node's copy of an entry changed.
		 *
		 * @param key      the entry's key
		 * @param previous the value before, or {@code null} when there was none
		 * @param current  the value now, or {@code null} when it was removed or expired
		 * @param origin   which node changed it
		 */
		void changed(@NotNull String key, @Nullable T previous, @Nullable T current, @NotNull Origin origin);
	}
}
