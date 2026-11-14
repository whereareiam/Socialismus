package me.whereareiam.socialismus.common.condition.type.recording;

import me.whereareiam.socialismus.store.Store;
import me.whereareiam.socialismus.store.StoreKey;
import me.whereareiam.socialismus.store.Stores;
import org.jetbrains.annotations.NotNull;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * When each player's cooldowns end, by player and cooldown, in a store that every server shares and that forgets
 * a cooldown once it ended, so a player meets the same cooldown on every server of a network.
 */
public final class Cooldowns {
	/**
	 * The store of the cooldowns: when each ends, by player and cooldown.
	 */
	public static final StoreKey<String, Instant> STORE = StoreKey.of("socialismus:cooldowns", String.class, Instant.class).replicated();

	private final Store<String, Instant> ends;
	private final Clock clock;

	/**
	 * Keeps the cooldowns in Socialismus's stores.
	 *
	 * @param stores the stores
	 * @param clock  the clock cooldowns run by
	 */
	public Cooldowns(@NotNull Stores stores, @NotNull Clock clock) {
		this.ends = stores.of(STORE);
		this.clock = clock;
	}

	/**
	 * Returns how long a player still waits for a cooldown.
	 *
	 * @param player   the player
	 * @param cooldown the cooldown's key
	 * @return the time left, empty when it ended or never started
	 */
	public @NotNull Optional<Duration> remaining(@NotNull UUID player, @NotNull String cooldown) {
		Instant now = clock.instant();

		return ends.get(key(player, cooldown))
				.filter(end -> end.isAfter(now))
				.map(end -> Duration.between(now, end));
	}

	/**
	 * Starts a player's cooldown, replacing one that runs.
	 *
	 * @param player   the player
	 * @param cooldown the cooldown's key
	 * @param duration how long it runs
	 */
	public void start(@NotNull UUID player, @NotNull String cooldown, @NotNull Duration duration) {
		ends.put(key(player, cooldown), clock.instant().plus(duration), duration);
	}

	private static String key(UUID player, String cooldown) {
		return player + "/" + cooldown;
	}
}
