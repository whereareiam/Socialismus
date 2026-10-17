package me.whereareiam.socialismus.platform.velocity.actor.player;

import lombok.Getter;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.position.Position;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for all Velocity SocialismusPlayer implementations.
 * Provides common functionality for wrapping Velocity Players.
 */
@Getter
public abstract class AbstractVelocitySocialismusPlayer extends SocialismusPlayer {
	/**
	 * The underlying Velocity player instance
	 */
	@NotNull
	private final Player velocityPlayer;

	/**
	 * The player's current server name.
	 * This is Velocity-specific and can be updated when the player changes servers.
	 */
	@Nullable
	private String server;

	/**
	 * Creates a new AbstractVelocitySocialismusPlayer wrapping a Velocity player.
	 *
	 * @param velocityPlayer The Velocity player to wrap
	 */
	protected AbstractVelocitySocialismusPlayer(@NotNull Player velocityPlayer) {
		super(
				velocityPlayer.getUniqueId(),
				velocityPlayer.getUsername()
		);
		this.velocityPlayer = velocityPlayer;
		// Initialize server from the player's current server if available
		this.server = velocityPlayer.getCurrentServer()
				.map(serverConnection -> serverConnection.getServerInfo().getName())
				.orElse(null);
	}

	@Override
	public void sendMessage(@NotNull Component message) {
		velocityPlayer.sendMessage(message);
	}

	@Override
	public boolean hasPermission(@NotNull String permission) {
		return velocityPlayer.hasPermission(permission);
	}

	@Override
	@NotNull
	public Audience getAudience() {
		return velocityPlayer;
	}

	@Override
	@Nullable
	public String getLocation() {
		// Not applicable on proxy servers
		return null;
	}

	@Override
	@Nullable
	public String getServer() {
		// Return cached server, or get it from the player if available
		if (server != null) {
			return server;
		}
		// Fallback to getting it directly from the player
		return velocityPlayer.getCurrentServer()
				.map(serverConnection -> serverConnection.getServerInfo().getName())
				.orElse(null);
	}

	@Override
	public void setServer(@Nullable String server) {
		this.server = server;
	}

	@Override
	@Nullable
	public Position getPosition() {
		// Velocity does not support getting player position directly
		return null;
	}

	@Override
	@Nullable
	public Position getEyePosition() {
		// Velocity does not support getting player eye position directly
		return null;
	}

	@Override
	public boolean isWithinRange(@NotNull SocialismusPlayer other, double range) {
		// On Velocity, we can only check if players are on the same server
		// For actual distance checking, we'd need backend server information
		// Note: range parameter is ignored on proxy servers
		String thisServer = this.getServer();
		String otherServer = other.getServer();
		
		return thisServer != null && thisServer.equals(otherServer);
	}

	@Override
	public void playSound(@NotNull String sound, float volume, float pitch) {
		// No-op on proxy servers - sounds are backend-specific
	}
}

