package me.whereareiam.socialismus.platform;

import lombok.Getter;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.position.Position;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Common base for all Bukkit-backed {@link SocialismusPlayer} implementations.
 * <p>
 * This class lives in the shared <code>platform-bukkit/common</code> module and
 * contains all logic that only depends on the Bukkit API (not Paper-specific APIs).
 * Concrete platform implementations (Bukkit and Paper) are responsible for
 * providing messaging and {@link Audience} integration.
 */
@Getter
public abstract class AbstractBukkitPlayerBase extends SocialismusPlayer {
	/**
	 * The underlying Bukkit player instance.
	 */
	@NotNull
	protected final Player bukkitPlayer;

	/**
	 * The player's current location (world name).
	 * This is backend-specific and can be updated when the player changes worlds.
	 */
	@Nullable
	private String location;

	/**
	 * Creates a new AbstractBukkitPlayerBase wrapping a Bukkit player.
	 *
	 * @param bukkitPlayer The Bukkit player to wrap
	 */
	protected AbstractBukkitPlayerBase(@NotNull Player bukkitPlayer) {
		super(
				bukkitPlayer.getUniqueId(),
				bukkitPlayer.getName()
		);
		this.bukkitPlayer = bukkitPlayer;
		this.location = bukkitPlayer.getWorld().getName();
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
	public void setLocation(@Nullable String location) {
		this.location = location;
	}

	@Override
	@Nullable
	public String getServer() {
		// Not applicable on backend servers
		return null;
	}

	@Override
	@Nullable
	public Position getPosition() {
		Location loc = bukkitPlayer.getLocation();
		return new Position(loc.getX(), loc.getY(), loc.getZ());
	}

	@Override
	@Nullable
	public Position getEyePosition() {
		Location loc = bukkitPlayer.getEyeLocation();
		return new Position(loc.getX(), loc.getY(), loc.getZ());
	}

	@Override
	public boolean isWithinRange(@NotNull SocialismusPlayer other, double range) {
		if (!(other instanceof AbstractBukkitPlayerBase otherBukkit)) return false;
		Player otherPlayer = otherBukkit.getBukkitPlayer();

		// Check if both players are in the same world before measuring distance
		if (!bukkitPlayer.getWorld().equals(otherPlayer.getWorld())) return false;

		return bukkitPlayer.getLocation().distanceSquared(otherPlayer.getLocation()) <= range * range;
	}

	@Override
	public void playSound(@NotNull String sound, float volume, float pitch) {
		NamespacedKey soundKey = parsesoundKey(sound);
		if (soundKey == null) return;
		
		Sound bukkitSound = Registry.SOUNDS.get(soundKey);
		if (bukkitSound == null) return;
		
		bukkitPlayer.playSound(bukkitPlayer.getLocation(), bukkitSound, volume, pitch);
	}

	private NamespacedKey parsesoundKey(String sound) {
		if (sound.contains(":")) return NamespacedKey.fromString(sound);
		if (sound.matches("[A-Z_]+")) return NamespacedKey.minecraft(sound.toLowerCase().replace('_', '.'));
		return NamespacedKey.minecraft(sound);
	}
}