package me.whereareiam.socialismus.service.chat;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Which chats each player has turned off for themselves, by the player's unique id and the chat's identifier.
 * Socialismus keeps this in memory, and in its database once one is registered.
 */
public interface ChatToggleStore {
	/**
	 * Returns whether a player has turned a chat off.
	 */
	boolean isOff(@NotNull UUID player, @NotNull String chat);

	/**
	 * Turns a chat off for a player, or back on.
	 */
	void setOff(@NotNull UUID player, @NotNull String chat, boolean off);
}
