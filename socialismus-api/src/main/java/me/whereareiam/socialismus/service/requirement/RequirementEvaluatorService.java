package me.whereareiam.socialismus.service.requirement;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.model.requirement.RequirementDenial;
import me.whereareiam.socialismus.model.requirement.RequirementResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Evaluates requirement groups against players, each requirement through the {@link RequirementValidation}
 * registered for its key.
 *
 * <p>A group combines the results of its requirements, in their configured order, by its operator, and a denied
 * group carries the {@link RequirementDenial} of the requirement that decided it:</p>
 * <ul>
 *   <li>{@code AND} is denied by its first unmet requirement, with that requirement's denial;</li>
 *   <li>{@code OR} is denied when no requirement is met, with the first denial one of them gave;</li>
 *   <li>{@code XOR} is denied with the first denial when none is met, and without one when several are;</li>
 *   <li>{@code NOT}, {@code NAND} and {@code NOR} are denied because requirements were met, so no requirement
 *   can say why.</li>
 * </ul>
 * <p>A denied group also carries its own {@link RequirementGroup#getReason() reason}, which is told before any
 * other wording; a blank one drops the denial, so the feature tells its generic notice.
 * {@link RequirementReasonService} words the result.</p>
 */
public interface RequirementEvaluatorService {
	/**
	 * Evaluates a group for a player, with the reason a denial is told.
	 *
	 * @param group  the group to evaluate; a missing or empty group always passes
	 * @param player the player to evaluate
	 * @return passed, or denied with the facts of the requirement that decided it and the group's own reason
	 */
	@NotNull
	RequirementResult evaluate(@Nullable RequirementGroup group, @NotNull SocialismusPlayer player);

	/**
	 * Returns whether a player meets a group, for callers that tell the player nothing.
	 *
	 * @param group  the group to evaluate; a missing or empty group always passes
	 * @param player the player to evaluate
	 * @return {@code true} when the player meets the group
	 */
	default boolean check(@Nullable RequirementGroup group, @NotNull SocialismusPlayer player) {
		return evaluate(group, player).isPassed();
	}
}
