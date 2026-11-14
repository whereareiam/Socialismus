package me.whereareiam.socialismus.module.mentionator.common.notify;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Reaches players this server does not host, such as through SocialismusNetwork: they can be tagged, and the server
 * that hosts them notifies them. Nothing waits for an answer.
 */
public interface RemoteTagging {
	/**
	 * Returns the players the other servers host, whom tags may name.
	 */
	@NotNull Collection<? extends SocialismusPlayer> players();

	/**
	 * Hands a tagged player's notifications to the server that hosts them.
	 *
	 * @param target   the tagged player's unique id
	 * @param delivery what that server needs to notify them
	 */
	void send(@NotNull UUID target, @NotNull TagDelivery delivery);

	/**
	 * Hands sounds to the server a player is on, for a proxy, which cannot play them.
	 *
	 * @param player the player's unique id
	 * @param sounds the sounds, in order
	 */
	void playSounds(@NotNull UUID player, @NotNull List<SoundCue> sounds);
}
