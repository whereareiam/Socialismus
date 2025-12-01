package me.whereareiam.socialismus.platform.paper.actor.player;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Paper-specific implementation of SocialismusPlayer for legacy command contexts.
 * Wraps a Bukkit Player and stores the original CommandSender for reverse mapping.
 * Used by LegacyPaperCommandManager.
 */
@Getter
public class PaperSocialismusCommandPlayer extends AbstractPaperSocialismusPlayer {
	/**
	 * The original CommandSender used to create this actor
	 */
	@NotNull
	private final CommandSender commandSender;

	/**
	 * Creates a new PaperSocialismusCommandPlayer wrapping a Bukkit actor with its CommandSender.
	 *
	 * @param bukkitPlayer  The Bukkit actor to wrap
	 * @param commandSender The original CommandSender
	 */
	public PaperSocialismusCommandPlayer(@NotNull Player bukkitPlayer, @NotNull CommandSender commandSender) {
		super(bukkitPlayer);
		this.commandSender = commandSender;
	}
}