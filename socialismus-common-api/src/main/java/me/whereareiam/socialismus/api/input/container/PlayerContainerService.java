package me.whereareiam.socialismus.api.input.container;

import me.whereareiam.socialismus.api.model.player.DummyPlayer;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Service interface for managing player data containers in the Socialismus plugin system.
 * This interface provides methods for storing, retrieving, and managing {@link DummyPlayer}
 * instances across different platform implementations.
 *
 * <p>The service maintains a collection of players and provides thread-safe operations
 * for adding, removing, updating, and querying player data.</p>
 */
public interface PlayerContainerService {
	/**
	 * Adds a new player to the container.
	 *
	 * @param dummyPlayer The player instance to add
	 */
	void addPlayer(DummyPlayer dummyPlayer);

	/**
	 * Removes a player from the container.
	 *
	 * @param uniqueId The UUID of the player to remove
	 */
	void removePlayer(UUID uniqueId);

	/**
	 * Updates an existing player's data in the container.
	 *
	 * @param uniqueId    The UUID of the player to updater
	 * @param dummyPlayer The new player data
	 */
	void updatePlayer(UUID uniqueId, DummyPlayer dummyPlayer);

	/**
	 * Checks if a player exists in the container.
	 *
	 * @param uniqueId The UUID of the player to check
	 * @return true if the player exists, false otherwise
	 */
	boolean hasPlayer(UUID uniqueId);

	/**
	 * Retrieves a player by their username.
	 *
	 * @param username The username of the player
	 * @return An Optional containing the player if found, empty otherwise
	 */
	Optional<DummyPlayer> getPlayer(String username);

	/**
	 * Retrieves a player by their UUID.
	 *
	 * @param uniqueId The UUID of the player
	 * @return An Optional containing the player if found, empty otherwise
	 */
	Optional<DummyPlayer> getPlayer(UUID uniqueId);

	/**
	 * Retrieves all players in the container.
	 *
	 * @return An unmodifiable set of all players
	 */
	Set<DummyPlayer> getPlayers();
}