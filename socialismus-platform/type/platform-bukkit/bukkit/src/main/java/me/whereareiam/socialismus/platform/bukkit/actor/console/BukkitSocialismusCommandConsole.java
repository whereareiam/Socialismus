package me.whereareiam.socialismus.platform.bukkit.actor.console;

import lombok.Getter;
import me.whereareiam.socialismus.platform.bukkit.audience.SenderAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit-specific implementation of Console for command contexts.
 * Wraps Bukkit ConsoleCommandSender and stores the original CommandSender for reverse mapping.
 * Used by LegacyPaperCommandManager.
 */
@Getter
public class BukkitSocialismusCommandConsole extends AbstractBukkitSocialismusConsole {
	/**
	 * The original CommandSender used to create this console
	 */
	@NotNull
	private final CommandSender commandSender;

	/**
	 * Creates a new BukkitSocialismusCommandConsole wrapping a console sender with its CommandSender.
	 *
	 * @param consoleSender The Bukkit console sender
	 * @param commandSender The original CommandSender
	 * @param audiences     The audiences that reach the sender
	 */
	public BukkitSocialismusCommandConsole(@NotNull ConsoleCommandSender consoleSender, @NotNull CommandSender commandSender, @NotNull SenderAudiences audiences) {
		super(consoleSender, audiences);
		this.commandSender = commandSender;
	}
}

