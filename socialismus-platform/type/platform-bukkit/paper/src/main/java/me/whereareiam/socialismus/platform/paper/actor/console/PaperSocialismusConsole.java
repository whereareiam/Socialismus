package me.whereareiam.socialismus.platform.paper.actor.console;

import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Paper-specific implementation of Console.
 * Wraps Bukkit ConsoleCommandSender for command execution.
 * This is the base variant without command context storage.
 */
public class PaperSocialismusConsole extends AbstractPaperSocialismusConsole {
	/**
	 * Creates a new PaperSocialismusConsole wrapping a console sender.
	 *
	 * @param consoleSender The Bukkit console sender
	 */
	public PaperSocialismusConsole(@NotNull ConsoleCommandSender consoleSender) {
		super(consoleSender);
	}
}