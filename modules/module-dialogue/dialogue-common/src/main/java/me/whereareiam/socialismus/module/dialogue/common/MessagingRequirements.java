package me.whereareiam.socialismus.module.dialogue.common;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;
import me.whereareiam.socialismus.type.chat.Participants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Checks the requirements Dialogue puts on the two participants of a private message: those of
 * {@code messaging.requirements}, which decide who may message whom, and those of each format.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MessagingRequirements {
	private final RequirementEvaluatorService evaluator;
	private final Provider<DialogueSettings> settings;

	/**
	 * Returns whether {@code messaging.requirements} let the sender write to the recipient.
	 *
	 * @param sender    the player who writes
	 * @param recipient the player written to, or {@code null} when they are not known to this server
	 */
	public boolean allows(@NotNull SocialismusPlayer sender, @Nullable SocialismusPlayer recipient) {
		return meets(settings.get().getMessaging().getRequirements(), sender, recipient);
	}

	/**
	 * Returns whether the participants meet the groups given for them. A participant without a group meets it; a
	 * recipient not known to this server meets only an empty group.
	 *
	 * @param requirements the group of each participant, or {@code null} for none
	 * @param sender       the player who writes
	 * @param recipient    the player written to, or {@code null} when they are not known to this server
	 */
	public boolean meets(
			@Nullable Map<Participants, RequirementGroup> requirements,
			@NotNull SocialismusPlayer sender,
			@Nullable SocialismusPlayer recipient
	) {
		if (requirements == null) return true;

		return meets(requirements.get(Participants.SENDER), sender)
				&& meets(requirements.get(Participants.RECIPIENT), recipient);
	}

	private boolean meets(@Nullable RequirementGroup group, @Nullable SocialismusPlayer player) {
		if (group == null || group.getGroups() == null || group.getGroups().isEmpty()) return true;
		if (player == null) return false;

		return evaluator.check(group, player);
	}
}
