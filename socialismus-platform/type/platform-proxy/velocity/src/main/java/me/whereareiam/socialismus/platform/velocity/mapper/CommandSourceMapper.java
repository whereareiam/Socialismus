package me.whereareiam.socialismus.platform.velocity.mapper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Actor;
import me.whereareiam.socialismus.platform.velocity.actor.console.VelocitySocialismusCommandConsole;
import me.whereareiam.socialismus.platform.velocity.actor.player.VelocitySocialismusCommandPlayer;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.SenderMapper;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class CommandSourceMapper implements SenderMapper<CommandSource, Actor> {
	private final PlayerRegistry playerRegistry;

	@Override
	public @NonNull Actor map(@NonNull CommandSource source) {
		// Handle console sender
		if (source instanceof ConsoleCommandSource consoleCommandSource)
			return new VelocitySocialismusCommandConsole(consoleCommandSource, source);

		if (source instanceof Player player) {
			VelocitySocialismusCommandPlayer mapped = new VelocitySocialismusCommandPlayer(player, source);
			playerRegistry.syncPlayerData(mapped);
			return mapped;
		}

		throw new UnsupportedOperationException("Unsupported command source type: " + source.getClass().getName());
	}

	@Override
	public @NonNull CommandSource reverse(@NonNull Actor actor) {
		// Only command-context types are supported for reverse mapping
		if (actor instanceof VelocitySocialismusCommandPlayer commandPlayer)
			return commandPlayer.getCommandSource();

		if (actor instanceof VelocitySocialismusCommandConsole commandConsole)
			return commandConsole.getCommandSource();

		// If base types are passed, it means they were created outside command context
		throw new UnsupportedOperationException(
				"Cannot reverse map base Actor types to CommandSource. " +
						"Base Actor types are for non-command contexts only. " +
						"Actor was not created by CommandSourceMapper."
		);
	}
}
