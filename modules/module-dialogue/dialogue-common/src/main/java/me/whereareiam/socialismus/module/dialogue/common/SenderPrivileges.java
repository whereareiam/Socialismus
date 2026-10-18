package me.whereareiam.socialismus.module.dialogue.common;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Decides what a sender's requirements allow their private message, on the server they write on, the only one where
 * those requirements can be checked: bypassing a recipient who turned messages off, not being ignored, and staying
 * hidden from spies. The message carries the answers to whichever server delivers it.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SenderPrivileges {
	private final Provider<DialogueSettings> settings;
	private final RequirementEvaluatorService evaluator;

	/**
	 * Records the sender's privileges on a message written on this server.
	 *
	 * @param message the message, whose sender is on this server
	 */
	public void grant(@NotNull Message message) {
		DialogueSettings current = settings.get();
		SocialismusPlayer sender = message.getSender();
		message.setBypassesToggle(meets(current.getControl().getToggle().getBypass(), sender));
		message.setExemptFromIgnore(meets(current.getControl().getIgnore().getExempt(), sender));
		message.setHiddenFromSpies(meets(current.getModeration().getSpy().getExempt(), sender));
	}

	/**
	 * Returns whether a player meets a group of Dialogue's settings. Nobody meets an absent group; a group without
	 * requirements is met by everyone, as everywhere in Socialismus.
	 */
	public boolean meets(@Nullable RequirementGroup group, @NotNull SocialismusPlayer player) {
		if (group == null) return false;

		return evaluator.check(group, player);
	}
}
