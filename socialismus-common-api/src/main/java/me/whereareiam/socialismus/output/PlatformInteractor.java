package me.whereareiam.socialismus.output;

import me.whereareiam.socialismus.type.BroadcastTarget;
import me.whereareiam.socialismus.type.Version;
import net.kyori.adventure.text.Component;

import java.util.List;

/**
 * Interface for platform-specific interactions in the Socialismus plugin system.
 * Provides methods for common server operations like broadcasting messages,
 * checking player permissions, and retrieving server information.
 *
 * <p>This interface abstracts platform-specific implementations (e.g., Bukkit, Velocity)
 * to ensure consistent behavior across different server platforms.</p>
 */
@SuppressWarnings("unused")
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
	 * Checks if a player has a specific permission.
	 *
	 * @param username   The player's username
	 * @param permission The permission to check
	 * @return true if the player has the permission, false otherwise
	 */
	boolean hasPermission(String username, String permission);

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