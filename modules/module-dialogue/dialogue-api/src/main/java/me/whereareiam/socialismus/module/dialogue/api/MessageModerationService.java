package me.whereareiam.socialismus.module.dialogue.api;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * What staff can do with private messages. Each method tells the player the outcome, or why there is none.
 */
public interface MessageModerationService {
	/**
	 * Makes a player see the private messages delivered on their server between other players, or stop. A message
	 * stays hidden when one of its participants meets {@code moderation.spy.exempt}, and spying is unavailable in
	 * privacy mode or with {@code moderation.spy.enabled} off. Who may run it is the command's permission.
	 *
	 * @param player the player
	 */
	void toggleSpy(@NotNull SocialismusPlayer player);
}
