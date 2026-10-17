package me.whereareiam.socialismus.platform.paper.actor.console;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Paper-specific implementation of Console for legacy command contexts.
 * Wraps Bukkit ConsoleCommandSender and stores the original CommandSender for reverse mapping.
 * Used by LegacyPaperCommandManager.
 */
@Getter
public class PaperSocialismusCommandConsole extends AbstractPaperSocialismusConsole {
	/**
	 * The original CommandSender used to create this console
	 */
	@NotNull
	private final CommandSender commandSender;

	/**
	 * Creates a new PaperSocialismusCommandConsole wrapping a console sender with its CommandSender.
	 *
	 * @param consoleSender The Bukkit console sender
	 * @param commandSender The original CommandSender
	 */
	public PaperSocialismusCommandConsole(@NotNull ConsoleCommandSender consoleSender, @NotNull CommandSender commandSender) {
		super(consoleSender);
		this.commandSender = commandSender;
	}
}