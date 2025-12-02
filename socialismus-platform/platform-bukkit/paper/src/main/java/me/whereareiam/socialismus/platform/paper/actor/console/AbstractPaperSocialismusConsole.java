package me.whereareiam.socialismus.platform.paper.actor.console;

import lombok.Getter;
import me.whereareiam.commandant.model.Console;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Abstract base class for all Paper Console implementations.
 * Provides common functionality for wrapping Bukkit ConsoleCommandSender.
 */
@Getter
public abstract class AbstractPaperSocialismusConsole implements Console {
	/**
	 * The underlying Bukkit console sender
	 */
	@NotNull
	private final ConsoleCommandSender consoleSender;

	/**
	 * Creates a new AbstractPaperSocialismusConsole wrapping a console sender.
	 *
	 * @param consoleSender The Bukkit console sender
	 */
	protected AbstractPaperSocialismusConsole(@NotNull ConsoleCommandSender consoleSender) {
		this.consoleSender = consoleSender;
	}

	@Override
	public void sendMessage(@NotNull Component message) {
		consoleSender.sendMessage(message);
	}

	@Override
	@NotNull
	public Locale getLocale() {
		return Locale.ENGLISH;
	}

	@Override
	@NotNull
	public Audience getAudience() {
		return consoleSender;
	}
}

