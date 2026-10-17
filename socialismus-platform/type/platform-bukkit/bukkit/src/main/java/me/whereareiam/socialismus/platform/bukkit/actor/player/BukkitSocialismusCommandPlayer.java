package me.whereareiam.socialismus.platform.bukkit.actor.player;

import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit-specific implementation of SocialismusPlayer for command contexts.
 * Wraps a Bukkit Player and stores the original CommandSender for reverse mapping.
 * Used by LegacyPaperCommandManager.
 */
@Getter
public class BukkitSocialismusCommandPlayer extends AbstractBukkitSocialismusPlayer {
	/**
	 * The original CommandSender used to create this actor
	 */
	@NotNull
	private final CommandSender commandSender;

	/**
	 * Creates a new BukkitSocialismusCommandPlayer wrapping a Bukkit player with its CommandSender.
	 *
	 * @param bukkitPlayer  The Bukkit player to wrap
	 * @param commandSender The original CommandSender
	 * @param audiences     The BukkitAudiences instance for Adventure API support
	 */
	public BukkitSocialismusCommandPlayer(@NotNull Player bukkitPlayer, @NotNull CommandSender commandSender, @NotNull BukkitAudiences audiences) {
		super(bukkitPlayer, audiences);
		this.commandSender = commandSender;
	}
}

