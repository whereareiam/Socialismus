package me.whereareiam.socialismus.platform.paper.actor.player;

import lombok.Getter;
import me.whereareiam.socialismus.platform.AbstractBukkitPlayerBase;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for all Paper SocialismusPlayer implementations.
 * Provides Paper-specific functionality for wrapping Bukkit Players.
 * <p>
 * Shared Bukkit-only player logic lives in {@link AbstractBukkitPlayerBase}.
 */
@Getter
public abstract class AbstractPaperSocialismusPlayer extends AbstractBukkitPlayerBase {

	/**
	 * Creates a new AbstractPaperSocialismusPlayer wrapping a Bukkit actor.
	 *
	 * @param paperPlayer The Bukkit actor to wrap
	 */
	protected AbstractPaperSocialismusPlayer(@NotNull Player paperPlayer) {
		super(paperPlayer);
	}

	@Override
	public void sendMessage(@NotNull Component message) {
		bukkitPlayer.sendMessage(message);
	}

	@Override
	public boolean hasPermission(@NotNull String permission) {
		return bukkitPlayer.hasPermission(permission);
	}

	@Override
	@NotNull
	public Audience getAudience() {
		return bukkitPlayer;
	}
}