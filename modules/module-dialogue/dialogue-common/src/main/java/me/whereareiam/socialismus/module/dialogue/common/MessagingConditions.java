package me.whereareiam.socialismus.module.dialogue.common;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.model.phrase.Phrase;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.common.condition.DialogueConditions;
import me.whereareiam.socialismus.service.condition.ConditionTexts;
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
public class MessagingConditions {
	private final DialogueConditions conditions;
	private final ConditionTexts conditionTexts;

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
	public @NotNull Judgement judge(
			@NotNull SocialismusPlayer sender,
			@Nullable SocialismusPlayer recipient,
			@NotNull String recipientName
	) {
		Verdict verdict = conditions.writes(sender, recipient);
		if (verdict instanceof Verdict.Passed passed) return new Judgement(true, null, passed);
		if (!(verdict instanceof Verdict.Failed failed)) return Judgement.REFUSED;

		Phrase reason = conditionTexts.denied(failed, DialogueConditions.PHRASE_SCOPE, Map.of("recipientName", recipientName))
				.orElse(null);

		return new Judgement(false, reason, null);
	}

	/**
	 * Whether a private message may be sent, and when not, why.
	 *
	 * @param allowed whether the message may be sent
	 * @param reason  why it may not, as the sender is told; {@code null} when allowed or nothing words it
	 * @param passed  the passed verdict, to {@link #record()} once the message is sent; {@code null} when refused
	 */
	public record Judgement(
			boolean allowed,
			@Nullable Phrase reason,
			@Nullable Verdict.Passed passed
	) {
		static final Judgement ALLOWED = new Judgement(true, null, null);
		static final Judgement REFUSED = new Judgement(false, null, null);

		/**
		 * Records what the lists that allowed the message record, such as a cooldown, once it was sent.
		 */
		public void record() {
			if (passed != null) passed.record();
		}
	}
}
