package me.whereareiam.socialismus.service.chat;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link ChatOwnership}s modules register. Socialismus handles a player's chat only when every registered
 * ownership agrees; with none registered, it handles every player it hosts. The platforms ask it before they take a
 * message, and chat asks it before it delivers one. A game server drops the original message of a player it does not
 * handle, since the proxy in front of it handles and shows it; a proxy passes it on untouched to the server that does.
 */
public interface ChatOwnershipService {
	/**
	 * Returns whether this Socialismus handles the player's chat.
	 *
	 * @param player a player this server hosts
	 */
	boolean handles(@NotNull SocialismusPlayer player);

	/**
	 * Registers an ownership.
	 */
	void register(@NotNull ChatOwnership ownership);

	/**
	 * Removes an ownership registered before, such as when the module that registered it unloads.
	 */
	void unregister(@NotNull ChatOwnership ownership);
}
