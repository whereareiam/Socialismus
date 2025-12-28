package me.whereareiam.socialismus.model.player;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.keystone.Player;
import me.whereareiam.socialismus.model.position.Position;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract base class for Socialismus player implementations.
 * Platform-specific modules (Paper, Velocity) extend this with concrete implementations.
 * <p>
 * Implements Player (Keystone) which extends Actor.
 */
@Getter
@ToString
@SuppressWarnings("unused")
public abstract class SocialismusPlayer implements Player {
	/**
	 * The player's unique identifier
	 */
	@NotNull
	protected final UUID uniqueId;

	/**
	 * The player's username
	 */
	@NotNull
	protected final String username;

	/**
	 * Dynamic custom data storage for modules to extend player functionality.
	 * Keys are namespaced to prevent conflicts between modules.
	 */
	private final Map<String, Object> customData = new ConcurrentHashMap<>();

	/**
	 * Static reference to PlayerRegistry for syncing data.
	 * Set by the service implementation during initialization.
	 */
	@Setter
	private static PlayerRegistry playerRegistry;

	/**
	 * Constructor for platform-specific implementations.
	 *
	 * @param uniqueId The player's UUID
	 * @param username The player's username
	 */
	protected SocialismusPlayer(
			@NotNull UUID uniqueId,
			@NotNull String username
	) {
		this.uniqueId = uniqueId;
		this.username = username;

		if (playerRegistry != null) playerRegistry.syncPlayerData(this);
	}

	/**
	 * Sends a message to this player.
	 * Platform-specific implementation required.
	 *
	 * @param message The message to send
	 */
	@Override
	public abstract void sendMessage(@NotNull Component message);

	/**
	 * Checks if this player has a specific permission.
	 * Platform-specific implementation required.
	 *
	 * @param permission The permission to check
	 * @return true if the player has the permission
	 */
	@Override
	public abstract boolean hasPermission(@NotNull String permission);

	/**
	 * Gets the player's preferred locale.
	 * Default implementation returns English locale as it's not used in Socialismus.
	 *
	 * @return The player's locale (defaults to English)
	 */
	@Override
	@NotNull
	public Locale getLocale() {
		return Locale.ENGLISH;
	}

	/**
	 * Gets the player's current location (world name).
	 * This is backend-specific (Bukkit/Paper) and returns null on proxy servers.
	 *
	 * @return The world name where the player is located, or null if not applicable
	 */
	@Nullable
	public abstract String getLocation();

	/**
	 * Sets the player's current location (world name).
	 * This is backend-specific (Bukkit/Paper) and is a no-op on proxy servers.
	 *
	 * @param location The world name where the player is located
	 */
	public void setLocation(@Nullable String location) {
		// Default implementation: no-op for platforms that don't support location
		// Overridden in backend-specific implementations
	}

	/**
	 * Gets the player's current server name.
	 * This is Velocity-specific and returns null on backend servers.
	 *
	 * @return The server name where the player is located, or null if not applicable
	 */
	@Nullable
	public abstract String getServer();

	/**
	 * Sets the player's current server name.
	 * This is Velocity-specific and is a no-op on backend servers.
	 *
	 * @param server The server name where the player is located
	 */
	public void setServer(@Nullable String server) {
		// Default implementation: no-op for platforms that don't support server
		// Overridden in Velocity-specific implementations
	}

	/**
	 * Gets the position of this player.
	 * This is backend-specific (Bukkit/Paper) and returns null on proxy servers.
	 *
	 * @return The player's position, or null if not applicable
	 */
	@Nullable
	public abstract Position getPosition();

	/**
	 * Gets the eye position of this player.
	 * This is backend-specific (Bukkit/Paper) and returns null on proxy servers.
	 *
	 * @return The player's eye position, or null if not applicable
	 */
	@Nullable
	public abstract Position getEyePosition();

	/**
	 * Checks if this player is within a specified range of another player.
	 *
	 * @param other The other player to check distance against
	 * @param range The maximum distance to check
	 * @return true if players are within range, false otherwise
	 */
	public abstract boolean isWithinRange(@NotNull SocialismusPlayer other, double range);

	/**
	 * Sets custom data for this player using a type-safe key.
	 *
	 * @param key the data key
	 * @param value the value to store (null to remove)
	 * @param <T> the value type
	 */
	public <T> void setData(@NotNull PlayerDataKey<T> key, @Nullable T value) {
		if (value == null) {
			customData.remove(key.getFullKey());
		} else {
			customData.put(key.getFullKey(), value);
		}
	}

	/**
	 * Gets custom data for this player using a type-safe key.
	 *
	 * @param key the data key
	 * @param <T> the value type
	 * @return the stored value, or null if not set
	 */
	@Nullable
	public <T> T getData(@NotNull PlayerDataKey<T> key) {
		Object value = customData.get(key.getFullKey());
		return key.getType().isInstance(value) ? key.getType().cast(value) : null;
	}

	/**
	 * Gets all custom data for this player.
	 * Used internally for syncing player data.
	 *
	 * @return a copy of the custom data map
	 */
	@NotNull
	public Map<String, Object> getCustomData() {
		return new HashMap<>(customData);
	}

	/**
	 * Sets all custom data for this player.
	 * Used internally for syncing player data.
	 *
	 * @param data the custom data to restore
	 */
	public void setCustomData(@NotNull Map<String, Object> data) {
		customData.clear();
		customData.putAll(data);
	}
}