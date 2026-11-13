package me.whereareiam.socialismus.service.condition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.whereareiam.conditia.model.Wording;
import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.model.phrase.Phrase;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * What a player is told about a condition, from the texts its lines carry and the phrases. When a list refuses a
 * player, they read, in this order:
 * <ol>
 *   <li>the {@code denied} text of the failed line, or of the nearest line around it that has one;</li>
 *   <li>the phrase of the reason's key under {@code denied.}, for the part the condition was about first, such as
 *       {@code denied.recipient.permission.missing}, then for anyone, such as {@code denied.permission.missing};
 *       each looked up in the feature's scope first, such as {@code chat.denied.permission.missing};</li>
 * </ol>
 * followed by the {@code hint} text of the failed line or the nearest around it. Both may refer to phrases as
 * {@code <ref:key>}; a blank phrase leaves the reason to the next.
 *
 * <pre>{@code
 * if (verdict instanceof Verdict.Failed failed)
 *     requirementTexts.denied(failed, "chat", Map.of("chat", chat.getId()))
 *             .ifPresent(phrase -> sender.sendMessage(Serializer.serialize(phrase.toContent(sender))));
 * }</pre>
 *
 * A failure nothing words, such as a failed {@code any} without a {@code denied} text, is told by the feature's own
 * generic notice, such as that no chat took the message.
 */
public interface ConditionTexts {
	/**
	 * Words a failed condition for the player it refused.
	 *
	 * @param failed       the verdict of the place
	 * @param scope        the feature's scope of phrases, such as {@code chat} or {@code dialogue}
	 * @param placeholders the feature's own placeholders, such as {@code {chat}}; the reason's values keep their
	 *                     meaning and are not replaced by them
	 * @return the phrase the player is told, empty when nothing words the failure
	 */
	@NotNull Optional<Phrase> denied(
			@NotNull Verdict.Failed failed,
			@NotNull String scope,
			@NotNull Map<String, String> placeholders
	);

	/**
	 * The words a line of a {@code requires:} list may carry a text under, beside its condition:
	 *
	 * <pre>{@code
	 * requires:
	 *   - permission: socialismus.chat.staff
	 *     denied: "<ref:staff-only>"
	 *     hint: "<gray>Ask a moderator for access."
	 * }</pre>
	 */
	@Getter
	@RequiredArgsConstructor
	enum Word {
		/**
		 * What the player is told when the line refuses them, instead of the {@code denied.*} phrase of the reason.
		 */
		DENIED(Wording.When.FAILURE),

		/**
		 * How the player can meet the line, told after why it refused them.
		 */
		HINT(Wording.When.FAILURE);

		/**
		 * When the word's text applies.
		 */
		private final @NotNull Wording.When when;

		/**
		 * The word as configs write it.
		 *
		 * @return the word, such as {@code denied}
		 */
		public @NotNull String word() {
			return name().toLowerCase(Locale.ROOT);
		}

		/**
		 * The word as Conditia is told it.
		 *
		 * @return the wording
		 */
		public @NotNull Wording wording() {
			return switch (when) {
				case FAILURE -> Wording.onFailure(word());
			};
		}
	}
}
