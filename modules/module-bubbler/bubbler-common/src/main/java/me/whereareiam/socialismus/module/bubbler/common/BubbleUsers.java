package me.whereareiam.socialismus.module.bubbler.common;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * Finds the PacketEvents user of a player. PacketEvents takes the server's own player; a player's audience is that
 * player only on the PAPER jar, so Bubbler looks the player up by its id.
 */
public final class BubbleUsers {
	private BubbleUsers() {
	}

	/**
	 * Returns the PacketEvents user of an online player.
	 *
	 * @param player the player
	 */
	public static User of(@NotNull SocialismusPlayer player) {
		return PacketEvents.getAPI().getPlayerManager().getUser(Bukkit.getPlayer(player.getUniqueId()));
	}
}
