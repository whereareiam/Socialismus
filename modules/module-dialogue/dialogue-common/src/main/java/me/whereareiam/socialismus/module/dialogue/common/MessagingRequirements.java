package me.whereareiam.socialismus.module.dialogue.common;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.RequirementReason;
import me.whereareiam.socialismus.module.dialogue.common.requirement.DialogueRequirements;
import me.whereareiam.socialismus.service.requirement.RequirementReasonService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Judges whether a private message may be sent: {@code messaging.requires} for the sender, then
 * {@code messaging.reaches} for the recipient, worded for the participant the failed condition was about through
 * Dialogue's phrases.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MessagingRequirements {
	private final DialogueRequirements requirements;
	private final RequirementReasonService reasonService;

	/**
	 * Judges whether the sender may write to the recipient. A refusal carries why, worded by the line's
	 * {@code denied} or by the {@code dialogue.denied.*} phrase of its reason, under {@code sender} or
	 * {@code recipient} for the participant the failed condition was about.
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
		me.whereareiam.conditia.result.Verdict verdict = requirements.writes(sender, recipient);
		if (verdict.passed()) return Verdict.ALLOWED;
		if (!(verdict instanceof me.whereareiam.conditia.result.Verdict.Failed failed)) return Verdict.REFUSED;

		RequirementReason reason = reasonService.resolve(failed, DialogueRequirements.PHRASE_SCOPE, Map.of("recipientName", recipientName))
				.orElse(null);

		return new Verdict(false, reason);
	}

	/**
	 * Whether a private message may be sent, and when not, why.
	 *
	 * @param allowed whether the message may be sent
	 * @param reason  why it may not, as the sender is told; {@code null} when allowed or nothing words it
	 */
	public record Verdict(boolean allowed, @Nullable RequirementReason reason) {
		static final Verdict ALLOWED = new Verdict(true, null);
		static final Verdict REFUSED = new Verdict(false, null);
	}
}
