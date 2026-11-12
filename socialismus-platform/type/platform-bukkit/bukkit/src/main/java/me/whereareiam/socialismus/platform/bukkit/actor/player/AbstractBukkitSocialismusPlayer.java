package me.whereareiam.socialismus.platform.bukkit.actor.player;

import lombok.Getter;
import me.whereareiam.socialismus.platform.bukkit.common.AbstractBukkitPlayerBase;
import net.kyori.adventure.audience.Audience;
import me.whereareiam.socialismus.platform.bukkit.audience.SenderAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for all Bukkit SocialismusPlayer implementations.
 * Provides Bukkit-specific functionality for wrapping Bukkit Players and
 * integrates with Adventure via {@link SenderAudiences}.
 * <p>
 * Shared Bukkit-only player logic lives in {@link AbstractBukkitPlayerBase}.
 */
@Getter
public abstract class AbstractBukkitSocialismusPlayer extends AbstractBukkitPlayerBase {
	/**
	 * The audiences that reach the sender
	 */
	@NotNull
	private final SenderAudiences audiences;

	/**
	 * Creates a new AbstractBukkitSocialismusPlayer wrapping a Bukkit player.
	 *
	 * @param bukkitPlayer The Bukkit player to wrap
	 * @param audiences    The audiences that reach the sender
	 */
	protected AbstractBukkitSocialismusPlayer(@NotNull Player bukkitPlayer, @NotNull SenderAudiences audiences) {
		super(bukkitPlayer);
		this.audiences = audiences;
	}

	@Override
	public void sendMessage(@NotNull Component message) {
		audiences.sender(bukkitPlayer).sendMessage(message);
	}

	@Override
	public boolean hasPermission(@NotNull String permission) {
		return bukkitPlayer.hasPermission(permission);
	}

	@Override
	@NotNull
	public Audience getAudience() {
		return audiences.sender(bukkitPlayer);
	}
}