package me.whereareiam.socialismus.store;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Names a {@link Store} and says what its values need: shared between the servers, kept across restarts, or
 * forgotten after a while. What a server does not have falls back to its memory, so a store works everywhere and
 * gains what the installed modules offer:
 * <pre>{@code
 * StoreKey<UUID, Mode> MODES = StoreKey.of("mentionator:modes", UUID.class, Mode.class).replicated().persistent();
 * StoreKey<String, Instant> COOLDOWNS = StoreKey.of("socialismus:cooldowns", String.class, Instant.class)
 *         .replicated()
 *         .expiring(Duration.ofHours(1));
 * }</pre>
 *
 * <p>Keys and values are written as JSON wherever they leave the server, so their types must survive that, as
 * {@code String}, {@code UUID}, numbers, enums and plain models do.
 *
 * @param <K> key type
 * @param <V> value type
 */
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class StoreKey<K, V> {
	private static final Pattern NAME = Pattern.compile("[a-z0-9-]+:[a-z0-9-]+");

	/**
	 * The store's name: its owner's id and the store, such as {@code mentionator:modes}.
	 */
	private final @NotNull String name;

	/**
	 * The type of the keys.
	 */
	private final @NotNull Class<K> keyType;

	/**
	 * The type of the values.
	 */
	private final @NotNull Class<V> valueType;

	/**
	 * Whether every server shares the values, when a module offers {@link StoreReplication}.
	 */
	private final boolean replicated;

	/**
	 * Whether the values outlive restarts, when a module offers {@link StorePersistence}.
	 */
	private final boolean persistent;

	private final @Nullable Duration expiry;

	/**
	 * Names a store whose values stay on this server, in memory, until they are removed.
	 *
	 * @param name      the owner's id and the store, such as {@code mentionator:modes}
	 * @param keyType   the type of the keys
	 * @param valueType the type of the values
	 * @param <K>       key type
	 * @param <V>       value type
	 * @return the key
	 * @throws IllegalArgumentException when the name is not lowercase {@code owner:store}
	 */
	public static <K, V> @NotNull StoreKey<K, V> of(@NotNull String name, @NotNull Class<K> keyType, @NotNull Class<V> valueType) {
		if (!NAME.matcher(name).matches())
			throw new IllegalArgumentException("A store's name is its owner and the store in lower case, such as mentionator:modes, not " + name);

		return new StoreKey<>(name, keyType, valueType, false, false, null);
	}

	/**
	 * Returns this key with values every server shares, as long as SocialismusNetwork offers replication; without it
	 * they stay on this server.
	 *
	 * @return the key
	 */
	public @NotNull StoreKey<K, V> replicated() {
		return new StoreKey<>(name, keyType, valueType, true, persistent, expiry);
	}

	/**
	 * Returns this key with values that outlive restarts, as long as SocialismusDatabase offers persistence; without
	 * it they are lost when the server, or with replication the last server, stops.
	 *
	 * @return the key
	 */
	public @NotNull StoreKey<K, V> persistent() {
		return new StoreKey<>(name, keyType, valueType, replicated, true, expiry);
	}

	/**
	 * Returns this key with values that are forgotten the given time after they were put, unless a put gives a time
	 * of its own.
	 *
	 * @param expiry how long a value lives
	 * @return the key
	 */
	public @NotNull StoreKey<K, V> expiring(@NotNull Duration expiry) {
		if (expiry.isNegative() || expiry.isZero())
			throw new IllegalArgumentException("A store's values live for a positive time, not " + expiry);

		return new StoreKey<>(name, keyType, valueType, replicated, persistent, expiry);
	}

	/**
	 * Returns how long a value lives when a put gives no time of its own.
	 *
	 * @return how long a value lives, empty until it is removed
	 */
	public @NotNull Optional<Duration> getExpiry() {
		return Optional.ofNullable(expiry);
	}
}
