package me.whereareiam.socialismus.store;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

/**
 * Shares the values of replicated stores between the servers, offered by a module through
 * {@link Stores#addReplication}, as SocialismusNetwork does over its transport. Each server keeps its own copy of the
 * entries, so reads never wait for the others.
 */
public interface StoreReplication {
	/**
	 * Returns the shared entries of one store. Asking twice for one name returns entries backed by the same values.
	 *
	 * @param name      the store's name, such as {@code mentionator:modes}
	 * @param valueType the type of the values
	 * @param <V>       value type
	 * @return the store's entries as every server shares them
	 */
	<V> @NotNull Entries<V> share(@NotNull String name, @NotNull Class<V> valueType);

	/**
	 * The entries of one store, by the text form of their keys.
	 *
	 * @param <V> value type
	 */
	interface Entries<V> {
		/**
		 * Returns this server's copy of a value.
		 *
		 * @param key the key's text
		 * @return the value, empty when there is none or it expired
		 */
		@NotNull Optional<V> get(@NotNull String key);

		/**
		 * Returns this server's copy of every value that has not expired.
		 *
		 * @return the values by the key's text
		 */
		@NotNull Map<String, V> entries();

		/**
		 * Sets a value for every server.
		 *
		 * @param key    the key's text
		 * @param value  the value
		 * @param expiry how long it lives, {@code null} until it is changed or removed
		 */
		void put(@NotNull String key, @NotNull V value, @Nullable Duration expiry);

		/**
		 * Removes a value for every server.
		 *
		 * @param key the key's text
		 */
		void remove(@NotNull String key);

		/**
		 * Calls a listener whenever a value changes, here or on another server.
		 *
		 * @param listener the listener, with the key's text
		 */
		void onChange(@NotNull Store.ChangeListener<String, V> listener);
	}
}
