package me.whereareiam.socialismus.platform.bukkit.actor.sender;

import lombok.Getter;
import me.whereareiam.keystone.Actor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

/**
 * Bukkit-specific actor for a command sender that is neither a player nor the console, such as a command block,
 * a remote console or the empty sender Spigot tests a player's commands with when the player joins.
 * Permissions and messages go to the sender itself.
 */
@Getter
public class BukkitSocialismusCommandSender implements Actor {
	/**
	 * The original CommandSender used to create this actor
	 */
	@NotNull
	private final CommandSender commandSender;

	/**
	 * The BukkitAudiences instance for Adventure API support
	 */
	@NotNull
	private final BukkitAudiences audiences;

	/**
	 * Creates a new BukkitSocialismusCommandSender wrapping a command sender.
	 *
	 * @param commandSender The original CommandSender
	 * @param audiences     The BukkitAudiences instance for Adventure API support
	 */
	public BukkitSocialismusCommandSender(@NotNull CommandSender commandSender, @NotNull BukkitAudiences audiences) {
		this.commandSender = commandSender;
		this.audiences = audiences;
	}

	/**
	 * A sender without an identity of its own is told apart by its kind and name.
	 */
	@Override
	@NotNull
	public UUID getUniqueId() {
		String identity = commandSender.getClass().getName() + ':' + commandSender.getName();
		return UUID.nameUUIDFromBytes(identity.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	@NotNull
	public String getUsername() {
		return commandSender.getName();
	}

	@Override
	public void sendMessage(@NotNull Component message) {
		audiences.sender(commandSender).sendMessage(message);
	}

	@Override
	public boolean hasPermission(@NotNull String permission) {
		return commandSender.hasPermission(permission);
	}

	@Override
	@NotNull
	public Locale getLocale() {
		return Locale.ENGLISH;
	}

	@Override
	@NotNull
	public Audience getAudience() {
		return audiences.sender(commandSender);
	}
}
