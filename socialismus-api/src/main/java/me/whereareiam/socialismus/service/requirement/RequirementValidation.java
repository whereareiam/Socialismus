package me.whereareiam.socialismus.service.requirement;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementDenial;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.model.requirement.RequirementKey;
import me.whereareiam.socialismus.model.requirement.RequirementResult;
import org.jetbrains.annotations.NotNull;

/**
 * Decides whether a player meets one type of requirement, the one registered under its {@link RequirementKey}.
 * Every requirement of a {@link RequirementGroup} is evaluated by the validation of its key, so a module adds a
 * requirement type of its own by registering a validation, and admins use it in any group, such as the sender
 * requirements of a chat, by its key.
 *
 * <p>A validation reports facts, not text: a denial names its key, how the player failed
 * ({@code REQUIRED} or {@code FORBIDDEN}) and placeholders that mean the same in every context. The feature that
 * checked the group words it through {@link RequirementReasonService}, so admins word {@code litebans:muted}
 * for chat in {@code chat/messages.yml} and for other features in theirs. A module may add a default message for
 * where nobody words its key.</p>
 *
 * <p>A module that keeps mutes of its own registers {@code litebans:muted}; admins put
 * {@code litebans:muted: { condition: HAS, expected: "false" }} into a chat's sender requirements, and a muted
 * player who writes there is told why the message went nowhere:</p>
 *
 * <pre>{@code
 * public class MutedRequirementValidation implements RequirementValidation {
 *     public static final RequirementKey<Requirement> MUTED =
 *             RequirementKey.create("litebans", "muted", Requirement.class);
 *
 *     private final MuteService mutes;
 *
 *     @Inject
 *     public MutedRequirementValidation(
 *             ExtendedRegistry<RequirementKey<?>, RequirementValidation> registry,
 *             MuteService mutes
 *     ) {
 *         this.mutes = mutes;
 *         registry.register(MUTED, this);
 *     }
 *
 *     @Override
 *     public @NotNull RequirementResult evaluate(@NotNull Requirement requirement, @NotNull SocialismusPlayer player) {
 *         Mute mute = mutes.find(player.getUniqueId());
 *         if (String.valueOf(mute != null).equals(requirement.getExpected()))
 *             return RequirementResult.passed();
 *         if (mute == null)
 *             return RequirementResult.denied(RequirementDenial.of(MUTED, RequirementFailure.REQUIRED, Map.of()));
 *
 *         return RequirementResult.denied(RequirementDenial.of(
 *                 MUTED,
 *                 RequirementFailure.FORBIDDEN,
 *                 Map.of("until", mute.until(), "reason", mute.reason())
 *         ).withMessage("<red>You are muted until {until}: {reason}"));
 *     }
 * }
 * }</pre>
 *
 * <p>A requirement with no fields beyond {@code condition} and {@code expected} is read as a plain
 * {@link Requirement}, as above.</p>
 *
 * @see RequirementDenial
 */
public interface RequirementValidation {
	/**
	 * Evaluates whether a player meets a requirement of this validation's type.
	 *
	 * @param requirement the requirement, as configured under this validation's key
	 * @param player      the player to evaluate
	 * @return passed, or denied with the facts of the denial, if there are any to tell
	 */
	@NotNull
	RequirementResult evaluate(@NotNull Requirement requirement, @NotNull SocialismusPlayer player);
}
