package me.whereareiam.socialismus.service.requirement;

import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.model.requirement.RequirementReason;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

/**
 * Words a failed requirement for the player it refused. What the player reads is, in this order:
 * <ol>
 *   <li>the {@code denied} text of the failed line, or of the nearest line around it that has one;</li>
 *   <li>the phrase of the reason's key under {@code denied.}, for the part the condition was about first, such as
 *       {@code denied.recipient.permission.missing}, then for anyone, such as {@code denied.permission.missing};
 *       each looked up in the feature's scope first, such as {@code chat.denied.permission.missing};</li>
 * </ol>
 * followed by the {@code hint} text of the failed line or the nearest around it. Both may refer to phrases as
 * {@code <ref:key>}.
 *
 * <pre>{@code
 * # phrases.yml
 * denied:
 *   world.outside: "{prefix}<white>This only works in <gray>{worlds}</gray>."
 * chat:
 *   denied:
 *     world.outside: "{prefix}<white><gray>{chat}</gray> chat only works in <gray>{worlds}</gray>."
 * }</pre>
 *
 * <pre>{@code
 * if (verdict instanceof Verdict.Failed failed) {
 *     Optional<RequirementReason> reason = reasonService.resolve(failed, "chat", Map.of("chat", chat.getId()));
 * }
 * }</pre>
 *
 * A failure that nothing words, such as a failed {@code any} without a {@code denied} text, is told by the
 * feature's own generic notice, such as that no chat took the message.
 */
public interface RequirementReasonService {
	/**
	 * Words a failed requirement for the player it refused.
	 *
	 * @param failed       the verdict of the place
	 * @param scope        the feature's scope of phrases, such as {@code chat} or {@code dialogue}
	 * @param placeholders the feature's own placeholders, such as {@code {chat}}; the reason's values keep their
	 *                     meaning and are not replaced by them
	 * @return the reason the player is told, empty when nothing words the failure
	 */
	@NotNull Optional<RequirementReason> resolve(
			@NotNull Verdict.Failed failed,
			@NotNull String scope,
			@NotNull Map<String, String> placeholders
	);
}
