package me.whereareiam.socialismus.store;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

/**
 * Values a feature keeps by key, obtained from {@link Stores#of}. Every server holds a working copy in memory, so
 * reads answer at once on any thread, such as a chat thread. Where its {@link StoreKey} asks for it and a module
 * offers it, changes are shared with the other servers and saved in the database in the background; the store
 * moves its values over when such a module comes or goes.
 *
 * <pre>{@code
 * Store<UUID, Mode> modes = stores.of(MODES);
 * modes.put(player.getUniqueId(), Mode.DIRECT);
 * Mode mode = modes.get(player.getUniqueId()).orElse(Mode.ALL);
 * }</pre>
 *
 * @param <K> key type
 * @param <V> value type
 */
public interface Store<K, V> {
	/**
	 * Returns the key the store was obtained with.
	 *
	 * @return the store's key
	 */
	@NotNull StoreKey<K, V> key();

	/**
	 * Returns this server's copy of a value.
	 *
	 * @param key the value's key
	 * @return the value, empty when there is none or it expired
	 */
	@NotNull Optional<V> get(@NotNull K key);

	/**
	 * Returns this server's copy of every value that has not expired. A persistent store holds the values the
	 * database had once it has loaded them, shortly after persistence is offered.
	 *
	 * @return the values by key
	 */
	@NotNull Map<K, V> entries();

	/**
	 * Sets a value, for as long as the store's {@linkplain StoreKey#getExpiry() expiry}, or until it is changed.
	 *
	 * @param key   the value's key
	 * @param value the value
	 */
	default void put(@NotNull K key, @NotNull V value) {
		put(key, value, key().getExpiry().orElse(null));
	}

	/**
	 * Sets a value for a time of its own.
	 *
	 * @param key    the value's key
	 * @param value  the value
	 * @param expiry how long the value lives, {@code null} until it is changed or removed
	 */
	void put(@NotNull K key, @NotNull V value, @Nullable Duration expiry);

	/**
	 * Removes a value.
	 *
	 * @param key the value's key
	 */
	void remove(@NotNull K key);

	/**
	 * Calls a listener whenever a value changes, on this server or, for a replicated store, on another.
	 *
	 * @param listener the listener
	 */
	void onChange(@NotNull ChangeListener<K, V> listener);

	/**
	 * Told of a changed value.
	 *
	 * @param <K> key type
	 * @param <V> value type
	 */
	@FunctionalInterface
	interface ChangeListener<K, V> {
		/**
		 * Called after a value changed.
		 *
		 * @param key      the value's key
		 * @param previous the value before, {@code null} when there was none
		 * @param current  the value now, {@code null} when it was removed or expired
		 */
		void changed(@NotNull K key, @Nullable V previous, @Nullable V current);
	}
}
