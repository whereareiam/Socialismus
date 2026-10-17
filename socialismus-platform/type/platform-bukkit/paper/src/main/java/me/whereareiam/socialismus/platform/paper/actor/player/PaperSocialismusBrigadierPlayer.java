package me.whereareiam.socialismus.platform.paper.actor.player;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Paper-specific implementation of SocialismusPaper for modern Brigadier command contexts.
 * Wraps a Bukkit Player and stores the original CommandSourceStack for reverse mapping.
 * Used by modern PaperCommandManager (1.20.5+).
 */
@Getter
public class PaperSocialismusBrigadierPlayer extends AbstractPaperSocialismusPlayer {
	/**
	 * The original CommandSourceStack used to create this actor
	 */
	@NotNull
	private final CommandSourceStack commandSourceStack;

	/**
	 * Creates a new PaperSocialismusBrigadierPlayer wrapping a Bukkit actor with its CommandSourceStack.
	 *
	 * @param bukkitPlayer       The Bukkit actor to wrap
	 * @param commandSourceStack The original CommandSourceStack
	 */
	public PaperSocialismusBrigadierPlayer(@NotNull Player bukkitPlayer, @NotNull CommandSourceStack commandSourceStack) {
		super(bukkitPlayer);
		this.commandSourceStack = commandSourceStack;
	}
}