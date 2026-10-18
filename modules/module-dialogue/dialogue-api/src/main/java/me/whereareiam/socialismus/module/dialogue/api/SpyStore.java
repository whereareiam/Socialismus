package me.whereareiam.socialismus.module.dialogue.api;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Which players spy on private messages, by their unique ids. Dialogue keeps this in memory, and in Socialismus's
 * database once one is registered.
 */
public interface SpyStore {
	/**
	 * Returns whether a player spies on private messages.
	 */
	boolean isSpying(@NotNull UUID player);

	/**
	 * Makes a player spy on private messages, or stop.
	 */
	void setSpying(@NotNull UUID player, boolean spying);
}
