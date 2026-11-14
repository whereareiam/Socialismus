package me.whereareiam.socialismus.service.chat;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * The conditions of {@code toggle} in {@code chat/settings.yml}. Before Socialismus has started, nobody meets them.
 */
public interface ChatToggleConditions {
	/**
	 * Checks whether a sender's messages reach players who turned the chat off: {@code toggle.bypass}.
	 *
	 * @param sender the sender
	 * @return whether the sender's messages reach them
	 */
	boolean bypasses(@NotNull SocialismusPlayer sender);
}
