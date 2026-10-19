package me.whereareiam.socialismus.module.dialogue.common.delivery;

import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import org.jetbrains.annotations.NotNull;

/**
 * Delivers private messages to players this server does not host, such as through SocialismusNetwork to the node a
 * player is on. It settles each message it takes, telling the sender whether it reached the recipient.
 */
public interface RemoteDelivery {
	/**
	 * Returns whether a player this server does not host can be reached.
	 *
	 * @param playerName the player's name, in any casing
	 */
	boolean mayReach(@NotNull String playerName);

	/**
	 * Sends a message to a recipient this server does not host.
	 *
	 * @param message the message, pending until the recipient's side answers
	 */
	void send(@NotNull Message message);
}
