package me.whereareiam.socialismus.platform.bukkit.mapper;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.keystone.Actor;
import me.whereareiam.socialismus.platform.bukkit.actor.console.BukkitSocialismusCommandConsole;
import me.whereareiam.socialismus.platform.bukkit.actor.player.BukkitSocialismusCommandPlayer;
import me.whereareiam.socialismus.platform.bukkit.actor.sender.BukkitSocialismusCommandSender;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.platform.bukkit.audience.SenderAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.SenderMapper;

@Singleton
public class CommandSenderMapper implements SenderMapper<CommandSender, Actor> {
	private final SenderAudiences audiences;
	private final PlayerRegistry playerRegistry;

	@Inject
	public CommandSenderMapper(SenderAudiences audiences, PlayerRegistry playerRegistry) {
		this.audiences = audiences;
		this.playerRegistry = playerRegistry;
	}

	@Override
	public @NonNull Actor map(@NonNull CommandSender source) {
		if (source instanceof ConsoleCommandSender consoleSender)
			return new BukkitSocialismusCommandConsole(consoleSender, source, audiences);

		if (source instanceof Player player) {
			BukkitSocialismusCommandPlayer mapped = new BukkitSocialismusCommandPlayer(player, source, audiences);
			playerRegistry.syncPlayerData(mapped);
			return mapped;
		}

		return new BukkitSocialismusCommandSender(source, audiences);
	}

	@Override
	public @NonNull CommandSender reverse(@NonNull Actor actor) {
		// Only command-context types are supported for reverse mapping
		if (actor instanceof BukkitSocialismusCommandPlayer commandPlayer)
			return commandPlayer.getCommandSender();

		if (actor instanceof BukkitSocialismusCommandConsole commandConsole)
			return commandConsole.getCommandSender();

		if (actor instanceof BukkitSocialismusCommandSender commandSender)
			return commandSender.getCommandSender();

		// If base types are passed, it means they were created outside command context
		throw new UnsupportedOperationException(
				"Cannot reverse map base Actor types to CommandSender. " +
						"Base Actor types are for non-command contexts only. " +
						"Actor was not created by CommandSenderMapper."
		);
	}
}