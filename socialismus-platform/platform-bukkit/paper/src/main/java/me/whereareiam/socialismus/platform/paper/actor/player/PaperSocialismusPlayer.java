package me.whereareiam.socialismus.platform.paper.actor.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Paper-specific implementation of SocialismusPlayer.
 * Wraps a Bukkit Player and provides direct access to platform-specific functionality.
 * This is the base variant without command context storage.
 */
public class PaperSocialismusPlayer extends AbstractPaperSocialismusPlayer {
	/**
	 * Creates a new PaperSocialismusPlayer wrapping a Bukkit actor.
	 *
	 * @param bukkitPlayer The Bukkit actor to wrap
	 */
	public PaperSocialismusPlayer(@NotNull Player bukkitPlayer) {
		super(bukkitPlayer);
	}
}