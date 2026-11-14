package me.whereareiam.socialismus.module.dialogue.common;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.common.condition.DialogueConditions;
import org.jetbrains.annotations.NotNull;

/**
 * Decides what a sender's conditions allow their private message, on the server they write on, the only one where
 * those conditions can be checked: bypassing a recipient who turned messages off, not being ignored, staying hidden
 * from spies, and the style of what they wrote. The message carries the answers to whichever server delivers it.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SenderPrivileges {
	private final DialogueConditions conditions;

	/**
	 * Records the sender's privileges on a message written on this server.
	 *
	 * @param message the message, whose sender is on this server
	 */
	public void grant(@NotNull Message message) {
		SocialismusPlayer sender = message.getSender();
		message.setBypassesToggle(conditions.bypassesToggle(sender));
		message.setExemptFromIgnore(conditions.exemptFromIgnore(sender));
		message.setHiddenFromSpies(conditions.hiddenFromSpies(sender));
		message.setStyle(conditions.style(sender).id());
	}

	/**
	 * Returns whether a player cannot be ignored: {@code control.ignore.exempt}.
	 *
	 * @param player the player
	 * @return whether nobody ignores them
	 */
	public boolean exemptFromIgnore(@NotNull SocialismusPlayer player) {
		return conditions.exemptFromIgnore(player);
	}

	/**
	 * Returns whether a player's private messages are hidden from spies: {@code moderation.spy.exempt}.
	 *
	 * @param player the player
	 * @return whether spies never see their messages
	 */
	public boolean hiddenFromSpies(@NotNull SocialismusPlayer player) {
		return conditions.hiddenFromSpies(player);
	}
}
