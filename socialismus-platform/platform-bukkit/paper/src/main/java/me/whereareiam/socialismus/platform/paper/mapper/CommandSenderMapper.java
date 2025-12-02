package me.whereareiam.socialismus.platform.paper.mapper;

import com.google.inject.Singleton;
import me.whereareiam.keystone.Actor;
import me.whereareiam.socialismus.platform.paper.actor.console.PaperSocialismusCommandConsole;
import me.whereareiam.socialismus.platform.paper.actor.console.PaperSocialismusConsole;
import me.whereareiam.socialismus.platform.paper.actor.player.PaperSocialismusCommandPlayer;
import me.whereareiam.socialismus.platform.paper.actor.player.PaperSocialismusPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.SenderMapper;

@Singleton
public class CommandSenderMapper implements SenderMapper<CommandSender, Actor> {

	@Override
	public @NonNull Actor map(@NonNull CommandSender source) {
		if (source instanceof ConsoleCommandSender consoleSender)
			return new PaperSocialismusCommandConsole(consoleSender, source);

		if (source instanceof Player player)
			return new PaperSocialismusCommandPlayer(player, source);

		throw new UnsupportedOperationException("Unsupported command sender type: " + source.getClass().getName());
	}

	@Override
	public @NonNull CommandSender reverse(@NonNull Actor actor) {
		// Only command-context types are supported for reverse mapping
		if (actor instanceof PaperSocialismusCommandPlayer commandPlayer)
			return commandPlayer.getCommandSender();

		if (actor instanceof PaperSocialismusCommandConsole commandConsole)
			return commandConsole.getCommandSender();

		// If base types are passed, it means they were created outside command context
		if (actor instanceof PaperSocialismusPlayer || actor instanceof PaperSocialismusConsole) {
			throw new UnsupportedOperationException(
					"Cannot reverse map base Actor types to CommandSender. " +
							"Base PaperSocialismusPlayer/Console are for non-command contexts only. " +
							"Actor was not created by CommandSenderMapper."
			);
		}

		throw new UnsupportedOperationException("Cannot reverse map Actor to CommandSender - unknown actor type: " + actor.getClass().getName());
	}
}
