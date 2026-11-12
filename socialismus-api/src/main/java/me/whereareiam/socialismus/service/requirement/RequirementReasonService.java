package me.whereareiam.socialismus.service.requirement;

import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.model.requirement.RequirementReason;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

/**
 * Words a failed requirement for the player: a condition reports what it found as a reason, and the feature that
 * checked it decides what the player reads. Texts are keyed by the reason's key, the condition's name and the
 * reason's in lower case with dashes, such as {@code permission.missing} or {@code world.inside}:
 *
 * <pre>{@code
 * # chat/messages.yml, for the requirements of the chats in chat/list/
 * requirements:
 *   world.outside: "{prefix}<gray>{chat}</gray> chat only works in {worlds}."
 *   world.inside: "{prefix}<gray>{chat}</gray> chat does not work in {world}."
 * }</pre>
 *
 * The first of these that has a text is told:
 * <ol>
 *   <li>the feature's own text for the reason's key;</li>
 *   <li>the text of {@code requirements} in {@code messages.yml}, which holds no feature's context.</li>
 * </ol>
 * A failure without a reason, such as a failed {@code any}, or one no text words, is told by the feature's own
 * generic notice, such as that no chat took the message.
 *
 * <pre>{@code
 * if (verdict instanceof Verdict.Failed failed) {
 *     Optional<RequirementReason> reason = reasonService.resolve(
 *             failed,
 *             chatMessages.getRequirements(),
 *             Map.of("chat", chat.getId())
 *     );
 * }
 * }</pre>
 */
public interface RequirementReasonService {
	/**
	 * Words a failed requirement for the player it refused.
	 *
	 * @param failed       the verdict of the place
	 * @param wording      the feature's texts, by reason key; empty for none
	 * @param placeholders the feature's own placeholders, such as {@code {chat}}; the reason's values keep their
	 *                     meaning and are not replaced by them
	 * @return the reason the player is told, empty when the failure has no reason or nothing words it
	 */
	@NotNull Optional<RequirementReason> resolve(
			@NotNull Verdict.Failed failed,
			@NotNull Map<String, String> wording,
			@NotNull Map<String, String> placeholders
	);
}
