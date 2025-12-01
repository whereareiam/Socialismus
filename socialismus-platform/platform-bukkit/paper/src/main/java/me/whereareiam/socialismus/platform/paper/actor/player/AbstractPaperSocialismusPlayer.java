package me.whereareiam.socialismus.platform.paper.actor.player;

import lombok.Getter;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.position.Position;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for all Paper SocialismusPlayer implementations.
 * Provides common functionality for wrapping Bukkit Players.
 */
@Getter
public abstract class AbstractPaperSocialismusPlayer extends SocialismusPlayer {
	/**
	 * The underlying Bukkit actor instance
	 */
	@NotNull
	private final Player paperPlayer;

	/**
	 * The player's current location (world name).
	 * This is backend-specific and can be updated when the player changes worlds.
	 */
	@Nullable
	private String location;

	/**
	 * Creates a new AbstractPaperSocialismusPlayer wrapping a Bukkit actor.
	 *
	 * @param paperPlayer The Bukkit actor to wrap
	 */
	protected AbstractPaperSocialismusPlayer(@NotNull Player paperPlayer) {
		super(
				paperPlayer.getUniqueId(),
				paperPlayer.getName()
		);
		this.paperPlayer = paperPlayer;
		this.location = paperPlayer.getWorld().getName();
	}

	@Override
	public void sendMessage(@NotNull Component message) {
		paperPlayer.sendMessage(message);
	}

	@Override
	public boolean hasPermission(@NotNull String permission) {
		return paperPlayer.hasPermission(permission);
	}

	@Override
	@NotNull
	public Audience getAudience() {
		return paperPlayer;
	}

	@Override
	@Nullable
	public String getLocation() {
		// Return cached location, or get it from the player if available
		if (location != null) {
			return location;
		}
		// Fallback to getting it directly from the player
		return paperPlayer.getWorld().getName();
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

	@Override
	@Nullable
	public Position getPosition() {
		Location loc = paperPlayer.getLocation();
		return new Position(loc.getX(), loc.getY(), loc.getZ());
	}

	@Override
	@Nullable
	public Position getEyePosition() {
		Location loc = paperPlayer.getEyeLocation();
		return new Position(loc.getX(), loc.getY(), loc.getZ());
	}

	@Override
	public boolean isWithinRange(@NotNull SocialismusPlayer other, double range) {
		if (!(other instanceof AbstractPaperSocialismusPlayer otherPaper)) return false;
		Player otherPlayer = otherPaper.getPaperPlayer();

		// Check if both players are in the same world before measuring distance
		if (!paperPlayer.getWorld().equals(otherPlayer.getWorld())) return false;

		return paperPlayer.getLocation().distanceSquared(otherPlayer.getLocation()) <= range * range;
	}
}

