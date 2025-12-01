package me.whereareiam.socialismus.platform.paper.actor.console;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.Getter;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Paper-specific implementation of Console for modern Brigadier command contexts.
 * Wraps Bukkit ConsoleCommandSender and stores the original CommandSourceStack for reverse mapping.
 * Used by modern PaperCommandManager (1.20.5+).
 */
@Getter
public class PaperSocialismusBrigadierConsole extends AbstractPaperSocialismusConsole {
	/**
	 * The original CommandSourceStack used to create this console
	 */
	@NotNull
	private final CommandSourceStack commandSourceStack;

	/**
	 * Creates a new PaperSocialismusBrigadierConsole wrapping a console sender with its CommandSourceStack.
	 *
	 * @param consoleSender      The Bukkit console sender
	 * @param commandSourceStack The original CommandSourceStack
	 */
	public PaperSocialismusBrigadierConsole(@NotNull ConsoleCommandSender consoleSender, @NotNull CommandSourceStack commandSourceStack) {
		super(consoleSender);
		this.commandSourceStack = commandSourceStack;
	}
}