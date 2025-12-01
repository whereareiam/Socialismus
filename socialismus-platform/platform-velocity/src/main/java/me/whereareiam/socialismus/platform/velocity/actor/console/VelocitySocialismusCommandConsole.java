package me.whereareiam.socialismus.platform.velocity.actor.console;

import com.velocitypowered.api.command.CommandSource;
import lombok.Getter;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import org.jetbrains.annotations.NotNull;

/**
 * Velocity-specific implementation of Console for command contexts.
 * Wraps Velocity ConsoleCommandSource and stores the original CommandSource for reverse mapping.
 * Used by VelocityCommandManager.
 */
@Getter
public class VelocitySocialismusCommandConsole extends AbstractVelocitySocialismusConsole {
	/**
	 * The original CommandSource used to create this console
	 */
	@NotNull
	private final CommandSource commandSource;

	/**
	 * Creates a new VelocitySocialismusCommandConsole wrapping a console command source with its CommandSource.
	 *
	 * @param consoleCommandSource The Velocity console command source
	 * @param commandSource        The original CommandSource
	 */
	public VelocitySocialismusCommandConsole(@NotNull ConsoleCommandSource consoleCommandSource, @NotNull CommandSource commandSource) {
		super(consoleCommandSource);
		this.commandSource = commandSource;
	}
}

