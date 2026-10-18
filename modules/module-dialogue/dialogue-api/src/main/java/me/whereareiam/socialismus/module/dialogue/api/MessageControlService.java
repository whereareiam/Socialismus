package me.whereareiam.socialismus.module.dialogue.api;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * What players decide about their own private messages: turning them off and ignoring other players. Each method
 * tells the player the outcome, or why there is none.
 */
public interface MessageControlService {
	/**
	 * Turns a player's private messages off, or back on when they are off. Messages of senders who meet
	 * {@code control.toggle.bypass} still reach them.
	 *
	 * @param player the player
	 */
	void toggleMessages(@NotNull SocialismusPlayer player);

	/**
	 * Makes a player ignore another, or stop ignoring them when they already do. An ignored player's private
	 * messages, and with {@code control.ignore.includesChat} their chat, no longer reach the player. Players who meet
	 * {@code control.ignore.exempt} cannot be ignored.
	 *
	 * @param player     the player who ignores
	 * @param targetName the name of the player to ignore, in any casing
	 */
	void ignore(@NotNull SocialismusPlayer player, @NotNull String targetName);

	/**
	 * Shows a player whom they ignore.
	 *
	 * @param player the player
	 */
	void showIgnored(@NotNull SocialismusPlayer player);
}
