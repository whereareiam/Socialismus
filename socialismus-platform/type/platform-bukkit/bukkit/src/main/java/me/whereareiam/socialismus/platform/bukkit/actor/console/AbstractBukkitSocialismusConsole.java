package me.whereareiam.socialismus.platform.bukkit.actor.console;

import lombok.Getter;
import me.whereareiam.commandant.model.Console;
import net.kyori.adventure.audience.Audience;
import me.whereareiam.socialismus.platform.bukkit.audience.SenderAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Abstract base class for all Bukkit Console implementations.
 * Provides common functionality for wrapping Bukkit ConsoleCommandSender.
 */
@Getter
public abstract class AbstractBukkitSocialismusConsole implements Console {
	/**
	 * The underlying Bukkit console sender
	 */
	@NotNull
	private final ConsoleCommandSender consoleSender;
	
	/**
	 * The audiences that reach the sender
	 */
	@NotNull
	private final SenderAudiences audiences;

	/**
	 * Creates a new AbstractBukkitSocialismusConsole wrapping a console sender.
	 *
	 * @param consoleSender The Bukkit console sender
	 * @param audiences     The audiences that reach the sender
	 */
	protected AbstractBukkitSocialismusConsole(@NotNull ConsoleCommandSender consoleSender, @NotNull SenderAudiences audiences) {
		this.consoleSender = consoleSender;
		this.audiences = audiences;
	}

	@Override
	public void sendMessage(@NotNull Component message) {
		audiences.sender(consoleSender).sendMessage(message);
	}

	@Override
	@NotNull
	public Locale getLocale() {
		return Locale.ENGLISH;
	}

	@Override
	@NotNull
	public Audience getAudience() {
		return audiences.sender(consoleSender);
	}
}

