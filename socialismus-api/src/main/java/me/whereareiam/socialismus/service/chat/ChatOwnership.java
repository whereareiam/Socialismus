package me.whereareiam.socialismus.service.chat;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Decides whether this Socialismus handles a player's chat: takes what they write and delivers what others write to
 * them. On its own, Socialismus handles every player it hosts. Where several Socialismus share players, such as a
 * proxy and its servers, a module registers an ownership with the {@link ChatOwnershipService}, so each player's
 * chat is handled exactly once.
 */
@FunctionalInterface
public interface ChatOwnership {
	/**
	 * Returns whether this Socialismus handles the player's chat.
	 *
	 * @param player a player this server hosts
	 */
	boolean handles(@NotNull SocialismusPlayer player);
}
