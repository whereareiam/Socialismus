package me.whereareiam.socialismus.service.requirement;

import me.whereareiam.socialismus.model.config.message.RequirementReasons;
import me.whereareiam.socialismus.model.requirement.RequirementDenial;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.model.requirement.RequirementReason;
import me.whereareiam.socialismus.model.requirement.RequirementResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Words a denial for the player: requirements report facts, and the feature that checked them decides what the
 * player reads. Every feature resolves its wording the same way, from its own messages file, where the wording sits
 * at the path of its requirement groups in its settings, below a participant and the requirement's key:
 *
 * <pre>{@code
 * # chat/messages.yml, for the requirements of chats in chat/list/
 * requirements:
 *   SENDER:
 *     socialismus:world:
 *       required: "{prefix}<gray>{chat}</gray> chat only works in {worlds}."
 *       forbidden: "{prefix}<gray>{chat}</gray> chat does not work in {world}."
 * }</pre>
 *
 * The first of these that has wording is told:
 * <ol>
 *   <li>the denying group's own {@link RequirementGroup#getReason() reason};</li>
 *   <li>the feature's wording for the requirement's key and how the player failed it;</li>
 *   <li>the wording of {@code requirements} in {@code messages.yml}, which holds no feature's context;</li>
 *   <li>the requirement's own {@link RequirementDenial#getMessage() message}, which a module's requirement may
 *   give.</li>
 * </ol>
 * When none has, the feature tells its own generic notice, such as that no chat took the message.
 *
 * <pre>{@code
 * RequirementResult result = requirementEvaluator.evaluate(sender, player);
 * RequirementReason reason = reasonService.resolve(
 *         result,
 *         chatMessages.getRequirements().get(Participants.SENDER),
 *         Map.of("chat", chat.getId())
 * );
 * player.sendMessage(Serializer.serialize(reason != null
 *         ? reason.toContent(player)
 *         : SerializerContent.builder().receiver(player).message(chatMessages.getNoChatMatch()).build()));
 * }</pre>
 */
public interface RequirementReasonService {
	/**
	 * Words a result for the player it denied.
	 *
	 * @param result       the result of the player's requirements
	 * @param wording      the feature's wording for one participant, by requirement key; {@code null} for none
	 * @param placeholders the feature's own placeholders, such as {@code {chat}}; the requirement's placeholders
	 *                     keep their meaning and are not replaced by them
	 * @return the reason the player is told, or {@code null} when the result passed or nothing words it
	 */
	@Nullable
	RequirementReason resolve(
			@NotNull RequirementResult result,
			@Nullable Map<String, RequirementReasons> wording,
			@NotNull Map<String, String> placeholders
	);
}
