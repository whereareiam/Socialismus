package me.whereareiam.socialismus.model.player;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Type-safe key for storing custom data on players.
 * Keys are namespaced to prevent conflicts between modules.
 *
 * @param <T> the type of value this key holds
 */
@Getter
@EqualsAndHashCode
public final class PlayerDataKey<T> {
	private final String namespace;
	private final String key;
	private final Class<T> type;

	private PlayerDataKey(@NotNull String namespace, @NotNull String key, @NotNull Class<T> type) {
		this.namespace = namespace;
		this.key = key;
		this.type = type;
	}

	/**
	 * Creates a new player data key.
	 *
	 * @param namespace the namespace (typically module name)
	 * @param key the key name
	 * @param type the value type class
	 * @param <T> the value type
	 * @return a new PlayerDataKey instance
	 */
	@NotNull
	public static <T> PlayerDataKey<T> create(@NotNull String namespace, @NotNull String key, @NotNull Class<T> type) {
		return new PlayerDataKey<>(namespace, key, type);
	}

	/**
	 * Gets the full namespaced key.
	 *
	 * @return the full key in format "namespace:key"
	 */
	@NotNull
	public String getFullKey() {
		return namespace + ":" + key;
	}
}
