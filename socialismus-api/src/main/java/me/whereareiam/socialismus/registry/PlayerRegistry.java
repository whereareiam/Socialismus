package me.whereareiam.socialismus.registry;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing player-specific data stored in memory.
 * Stores data in SocialismusPlayer instances and provides centralized access.
 */
@SuppressWarnings("unused")
public interface PlayerRegistry {
	/**
	 * Gets an SocialismusPlayer instance for the given UUID if it exists.
	 * <p>
	 * Player data is automatically stored when real SocialismusPlayer instances
	 * are created (e.g., when players execute commands).
	 *
	 * @param playerId The UUID of the player
	 * @return Optional containing the SocialismusPlayer instance if it exists, empty otherwise
	 */
	@NotNull
	Optional<SocialismusPlayer> getPlayerData(@NotNull UUID playerId);

	/**
	 * Gets an SocialismusPlayer instance by username if it exists.
	 * Lookup is case-insensitive and matches the most recent known username.
	 *
	 * @param username The player's username
	 * @return Optional containing the SocialismusPlayer if present, empty otherwise
	 */
	@NotNull
	Optional<SocialismusPlayer> getPlayerData(@NotNull String username);

	/**
	 * Removes player data for the given player.
	 * Useful for cleanup when a player disconnects.
	 *
	 * @param playerId The UUID of the player
	 */
	void removePlayerData(@NotNull UUID playerId);

	/**
	 * Checks if player data exists for the given player.
	 *
	 * @param playerId The UUID of the player
	 * @return true if player data exists, false otherwise
	 */
	boolean hasPlayerData(@NotNull UUID playerId);

	/**
	 * Syncs data from an existing SocialismusPlayer instance into storage.
	 * Useful when you have a platform-specific SocialismusPlayer instance
	 * and want to persist its data.
	 *
	 * @param player The SocialismusPlayer instance to sync
	 */
	void syncPlayerData(@NotNull SocialismusPlayer player);

	/**
	 * Returns all stored player entries.
	 *
	 * @return collection of players currently tracked by the registry
	 */
	@NotNull
	Collection<SocialismusPlayer> getPlayers();
}

