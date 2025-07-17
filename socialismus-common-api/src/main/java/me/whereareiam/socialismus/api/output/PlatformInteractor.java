package me.whereareiam.socialismus.api.output;

import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.position.Position;
import me.whereareiam.socialismus.api.type.BroadcastTarget;
import me.whereareiam.socialismus.api.type.Version;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.UUID;

/**
 * Interface for platform-specific interactions in the Socialismus plugin system.
 * Provides methods for common server operations like broadcasting messages,
 * checking player permissions, and retrieving server information.
 *
 * <p>This interface abstracts platform-specific implementations (e.g., Bukkit, Velocity)
 * to ensure consistent behavior across different server platforms.</p>
 */
public interface PlatformInteractor {
	/**
	 * Broadcasts a message to the specified target(s).
	 *
	 * @param component The message to broadcast
	 * @param target    Where to send it: ALL (players+console), PLAYERS, or CONSOLE.
	 */
	void broadcast(Component component, BroadcastTarget target);

	/**
	 * Shorthand for broadcast(component, ALL);
	 */
	default void broadcast(Component component) {
		broadcast(component, BroadcastTarget.ALL);
	}

	/**
	 * Gets the eye position of a player.
	 *
	 * @param dummyPlayer The dummy player instance
	 * @return The eye position of the player as a Position object
	 */
	Position getEyePosition(DummyPlayer dummyPlayer);

	/**
	 * Checks if two players are within a specified range of each other.
	 *
	 * @param player1 UUID of the first player
	 * @param player2 UUID of the second player
	 * @param range   The maximum distance to check
	 * @return true if players are within range, false otherwise
	 */
	boolean areWithinRange(UUID player1, UUID player2, double range);

	/**
	 * Gets the position of a player.
	 *
	 * @param dummyPlayer The dummy player instance
	 * @return An array of doubles representing the player's position (x, y, z)
	 */
	Position getPosition(DummyPlayer dummyPlayer);

	/**
	 * Checks if a player has a specific permission.
	 *
	 * @param username   The player's username
	 * @param permission The permission to check
	 * @return true if the player has the permission, false otherwise
	 */
	boolean hasPermission(String username, String permission);

	/**
	 * Checks if a dummy player has a specific permission.
	 *
	 * @param dummyPlayer The dummy player instance
	 * @param permission  The permission to check
	 * @return true if the dummy player has the permission, false otherwise
	 */
	boolean hasPermission(DummyPlayer dummyPlayer, String permission);

	/**
	 * Gets a list of all online players' usernames.
	 *
	 * @return A list of usernames of online players
	 */
	List<String> getOnlinePlayers();

	/**
	 * Gets the current number of online players.
	 *
	 * @return The number of online players
	 */
	int getOnlinePlayersCount();

	/**
	 * Gets the current server version.
	 *
	 * @return The server version information
	 */
	Version getServerVersion();
}