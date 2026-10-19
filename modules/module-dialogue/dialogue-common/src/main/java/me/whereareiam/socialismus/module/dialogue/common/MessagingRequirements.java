package me.whereareiam.socialismus.module.dialogue.common;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.config.message.RequirementReasons;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.model.requirement.RequirementReason;
import me.whereareiam.socialismus.model.requirement.RequirementResult;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueMessages;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;
import me.whereareiam.socialismus.service.requirement.RequirementReasonService;
import me.whereareiam.socialismus.type.chat.Participants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Checks the requirements Dialogue puts on the two participants of a private message: those of
 * {@code messaging.requirements}, which decide who may message whom, and those of each format.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MessagingRequirements {
	private final RequirementEvaluatorService evaluator;
	private final RequirementReasonService reasonService;
	private final Provider<DialogueSettings> settings;
	private final Provider<DialogueMessages> messages;

	/**
	 * Judges whether {@code messaging.requirements} let the sender write to the recipient, checking the sender's
	 * group first. A refusal carries why, worded by {@code messaging.requirements} in {@code messages} for the
	 * participant that failed.
	 *
	 * @param sender        the player who writes
	 * @param recipient     the player written to, or {@code null} when they are not known to this server
	 * @param recipientName the recipient's name, for {@code {recipientName}}
	 * @return whether the message may be sent, and why not
	 */
	public @NotNull Verdict judge(
			@NotNull SocialismusPlayer sender,
			@Nullable SocialismusPlayer recipient,
			@NotNull String recipientName
	) {
		Map<Participants, RequirementGroup> requirements = settings.get().getMessaging().getRequirements();
		if (requirements == null) return Verdict.ALLOWED;

		Map<String, String> placeholders = Map.of("recipientName", recipientName);
		for (Participants participant : List.of(Participants.SENDER, Participants.RECIPIENT)) {
			SocialismusPlayer player = participant == Participants.SENDER ? sender : recipient;
			RequirementResult result = evaluate(requirements.get(participant), player);
			if (result.isPassed()) continue;

			return new Verdict(false, reasonService.resolve(result, wordingFor(participant), placeholders));
		}

		return Verdict.ALLOWED;
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

	private RequirementResult evaluate(@Nullable RequirementGroup group, @Nullable SocialismusPlayer player) {
		if (group == null || group.getGroups() == null || group.getGroups().isEmpty()) return RequirementResult.passed();
		// A recipient this server does not know cannot be checked, and nothing can say why.
		if (player == null) return RequirementResult.denied();

		return evaluator.evaluate(group, player);
	}

	private @Nullable Map<String, RequirementReasons> wordingFor(Participants participant) {
		Map<Participants, Map<String, RequirementReasons>> wording = messages.get().getMessaging().getRequirements();

		return wording == null ? null : wording.get(participant);
	}

	/**
	 * Whether a private message may be sent, and when not, why.
	 *
	 * @param allowed whether the message may be sent
	 * @param reason  why it may not, as the sender is told; {@code null} when allowed or nothing words it
	 */
	public record Verdict(boolean allowed, @Nullable RequirementReason reason) {
		static final Verdict ALLOWED = new Verdict(true, null);
	}
}
