package me.whereareiam.socialismus.module.dialogue.api;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Which players have turned private messages off, by their unique ids. Every player takes them until they turn them
 * off. Dialogue keeps this in memory, and in Socialismus's database once one is registered.
 */
public interface MessageToggleStore {
	/**
	 * Returns whether a player has turned private messages off.
	 */
	boolean isOff(@NotNull UUID player);

	/**
	 * Turns a player's private messages off or back on.
	 */
	void setOff(@NotNull UUID player, boolean off);
}
