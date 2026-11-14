package me.whereareiam.socialismus.service.chat;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * The conditions of {@code history} in {@code chat/settings.yml}: who may clear messages from chat history, and whose
 * messages nobody clears. Before Socialismus has started, nobody meets them.
 */
public interface ChatHistoryConditions {
	/**
	 * Checks whether a player may clear messages from chat history: {@code history.requires}.
	 *
	 * @param viewer the player who sees the messages
	 * @return whether the player sees the clear button
	 */
	boolean clears(@NotNull SocialismusPlayer viewer);

	/**
	 * Checks whether nobody clears a sender's messages: {@code history.bypass}.
	 *
	 * @param sender the sender
	 * @return whether the sender's messages stay
	 */
	boolean keeps(@NotNull SocialismusPlayer sender);
}
