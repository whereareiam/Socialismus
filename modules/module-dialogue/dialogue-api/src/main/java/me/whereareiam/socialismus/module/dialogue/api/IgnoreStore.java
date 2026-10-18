package me.whereareiam.socialismus.module.dialogue.api;

import me.whereareiam.socialismus.module.dialogue.api.model.IgnoredPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Whom each player ignores, by unique ids. Dialogue keeps this in memory, and in Socialismus's database once one is
 * registered.
 */
public interface IgnoreStore {
	/**
	 * Returns whether a player ignores another.
	 */
	boolean ignores(@NotNull UUID player, @NotNull UUID other);

	/**
	 * Returns the players a player ignores, in the order they were ignored.
	 */
	@NotNull List<IgnoredPlayer> ignored(@NotNull UUID player);

	/**
	 * Makes a player ignore another.
	 *
	 * @return whether they did not ignore them before
	 */
	boolean ignore(@NotNull UUID player, @NotNull IgnoredPlayer other);

	/**
	 * Makes a player stop ignoring another.
	 *
	 * @return whether they ignored them
	 */
	boolean unignore(@NotNull UUID player, @NotNull UUID other);
}
