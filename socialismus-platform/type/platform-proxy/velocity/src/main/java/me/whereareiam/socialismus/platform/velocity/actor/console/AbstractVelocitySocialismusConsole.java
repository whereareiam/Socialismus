package me.whereareiam.socialismus.platform.velocity.actor.console;

import lombok.Getter;
import me.whereareiam.commandant.model.Console;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Abstract base class for all Velocity Console implementations.
 * Provides common functionality for wrapping Velocity ConsoleCommandSource.
 */
@Getter
public abstract class AbstractVelocitySocialismusConsole implements Console {
	/**
	 * The underlying Velocity console command source
	 */
	@NotNull
	private final ConsoleCommandSource consoleCommandSource;

	/**
	 * Creates a new AbstractVelocitySocialismusConsole wrapping a console command source.
	 *
	 * @param consoleCommandSource The Velocity console command source
	 */
	protected AbstractVelocitySocialismusConsole(@NotNull ConsoleCommandSource consoleCommandSource) {
		this.consoleCommandSource = consoleCommandSource;
	}

	@Override
	public void sendMessage(@NotNull Component message) {
		consoleCommandSource.sendMessage(message);
	}

	@Override
	@NotNull
	public Locale getLocale() {
		return Locale.ENGLISH;
	}

	@Override
	@NotNull
	public Audience getAudience() {
		return consoleCommandSource;
	}
}

