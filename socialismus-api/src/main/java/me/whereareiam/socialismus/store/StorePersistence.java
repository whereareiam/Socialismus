package me.whereareiam.socialismus.store;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Keeps the values of persistent stores across restarts, offered by a module through {@link Stores#addPersistence},
 * as SocialismusDatabase does in its database. A store loads its values once when persistence is offered and saves
 * every change of this server in the background, so no read waits for it. Keys and values arrive as text, as JSON.
 */
public interface StorePersistence {
	/**
	 * Loads every value of a store that has not expired.
	 *
	 * @param store the store's name, such as {@code mentionator:modes}
	 * @return the values by the key's text
	 */
	@NotNull CompletableFuture<Map<String, Saved>> load(@NotNull String store);

	/**
	 * Saves a value, replacing the one its key had.
	 *
	 * @param store the store's name
	 * @param key   the key's text
	 * @param saved the value's text and when it expires
	 * @return completion of the save
	 */
	@NotNull CompletableFuture<Void> save(@NotNull String store, @NotNull String key, @NotNull Saved saved);

	/**
	 * Deletes a value.
	 *
	 * @param store the store's name
	 * @param key   the key's text
	 * @return completion of the deletion
	 */
	@NotNull CompletableFuture<Void> delete(@NotNull String store, @NotNull String key);

	/**
	 * A value as persistence keeps it.
	 *
	 * @param value     the value as JSON
	 * @param expiresAt when it expires, {@code null} never
	 */
	record Saved(@NotNull String value, @Nullable Instant expiresAt) {
		/**
		 * Returns whether the value has expired at an instant.
		 *
		 * @param now the instant
		 * @return whether it has expired
		 */
		public boolean expiredAt(@NotNull Instant now) {
			return expiresAt != null && !expiresAt.isAfter(now);
		}
	}
}
