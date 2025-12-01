package me.whereareiam.socialismus.platform.bukkit.mapper;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.keystone.Actor;
import me.whereareiam.socialismus.platform.bukkit.actor.console.BukkitSocialismusCommandConsole;
import me.whereareiam.socialismus.platform.bukkit.actor.player.BukkitSocialismusCommandPlayer;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.SenderMapper;

@Singleton
public class CommandSenderMapper implements SenderMapper<CommandSender, Actor> {
	private final Provider<BukkitAudiences> audiences;

	@Inject
	public CommandSenderMapper(Provider<BukkitAudiences> audiences) {
		this.audiences = audiences;
	}

	@Override
	public @NonNull Actor map(@NonNull CommandSender source) {
		BukkitAudiences bukkitAudiences = audiences.get();
		
		if (source instanceof ConsoleCommandSender consoleSender)
			return new BukkitSocialismusCommandConsole(consoleSender, source, bukkitAudiences);

		if (source instanceof Player player)
			return new BukkitSocialismusCommandPlayer(player, source, bukkitAudiences);

		throw new UnsupportedOperationException("Unsupported command sender type: " + source.getClass().getName());
	}

	@Override
	public @NonNull CommandSender reverse(@NonNull Actor actor) {
		// Only command-context types are supported for reverse mapping
		if (actor instanceof BukkitSocialismusCommandPlayer commandPlayer)
			return commandPlayer.getCommandSender();

		if (actor instanceof BukkitSocialismusCommandConsole commandConsole)
			return commandConsole.getCommandSender();

		// If base types are passed, it means they were created outside command context
		throw new UnsupportedOperationException(
				"Cannot reverse map base Actor types to CommandSender. " +
						"Base Actor types are for non-command contexts only. " +
						"Actor was not created by CommandSenderMapper."
		);
	}
}