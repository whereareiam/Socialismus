package me.whereareiam.socialismus.platform.bukkit.actor.player;

import lombok.Getter;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for all Bukkit SocialismusPlayer implementations.
 * Provides common functionality for wrapping Bukkit Players.
 */
@Getter
public abstract class AbstractBukkitSocialismusPlayer extends SocialismusPlayer {
	/**
	 * The underlying Bukkit player instance
	 */
	@NotNull
	private final Player bukkitPlayer;
	
	/**
	 * The BukkitAudiences instance for Adventure API support
	 */
	@NotNull
	private final BukkitAudiences audiences;

	/**
	 * The player's current location (world name).
	 * This is backend-specific and can be updated when the player changes worlds.
	 */
	@Nullable
	private String location;

	/**
	 * Creates a new AbstractBukkitSocialismusPlayer wrapping a Bukkit player.
	 *
	 * @param bukkitPlayer The Bukkit player to wrap
	 * @param audiences    The BukkitAudiences instance for Adventure API support
	 */
	protected AbstractBukkitSocialismusPlayer(@NotNull Player bukkitPlayer, @NotNull BukkitAudiences audiences) {
		super(
				bukkitPlayer.getUniqueId(),
				bukkitPlayer.getName()
		);
		this.bukkitPlayer = bukkitPlayer;
		this.audiences = audiences;
		this.location = bukkitPlayer.getWorld().getName();
	}

	@Override
	public void sendMessage(@NotNull Component message) {
		audiences.player(bukkitPlayer).sendMessage(message);
	}

	@Override
	public boolean hasPermission(@NotNull String permission) {
		return bukkitPlayer.hasPermission(permission);
	}

	@Override
	@NotNull
	public Audience getAudience() {
		return audiences.player(bukkitPlayer);
	}

	@Override
	@Nullable
	public String getLocation() {
		// Return cached location, or get it from the player if available
		if (location != null) {
			return location;
		}
		// Fallback to getting it directly from the player
		return bukkitPlayer.getWorld().getName();
	}

	@Override
	@Nullable
	public String getServer() {
		// Not applicable on backend servers
		return null;
	}

	@Override
	public void setLocation(@Nullable String location) {
		this.location = location;
	}
}

