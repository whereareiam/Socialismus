package me.whereareiam.socialismus.service.chat;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Players turning chats off for themselves: a chat a player turned off no longer reaches them, unless its sender
 * meets {@code toggle.bypass} of {@code chat/settings}.
 */
public interface ChatToggleService {
	/**
	 * Turns a chat off for a player, or back on when it is off, and tells them. The player is told instead when the
	 * chat does not exist, cannot be turned off, or {@code toggle.enabled} is off.
	 *
	 * @param player the player
	 * @param chatId the chat's identifier
	 */
	void toggle(@NotNull SocialismusPlayer player, @NotNull String chatId);
}
