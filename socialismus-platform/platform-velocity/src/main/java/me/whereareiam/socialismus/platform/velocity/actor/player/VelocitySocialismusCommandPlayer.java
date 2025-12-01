package me.whereareiam.socialismus.platform.velocity.actor.player;

import com.velocitypowered.api.command.CommandSource;
import lombok.Getter;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Velocity-specific implementation of SocialismusPlayer for command contexts.
 * Wraps a Velocity Player and stores the original CommandSource for reverse mapping.
 * Used by VelocityCommandManager.
 */
@Getter
public class VelocitySocialismusCommandPlayer extends AbstractVelocitySocialismusPlayer {
	/**
	 * The original CommandSource used to create this actor
	 */
	@NotNull
	private final CommandSource commandSource;

	/**
	 * Creates a new VelocitySocialismusCommandPlayer wrapping a Velocity player with its CommandSource.
	 *
	 * @param velocityPlayer The Velocity player to wrap
	 * @param commandSource  The original CommandSource
	 */
	public VelocitySocialismusCommandPlayer(@NotNull Player velocityPlayer, @NotNull CommandSource commandSource) {
		super(velocityPlayer);
		this.commandSource = commandSource;
	}
}

