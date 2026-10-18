package me.whereareiam.socialismus.model.requirement;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Whether a player meets a requirement or a {@link RequirementGroup}: passed, or denied. A denial may carry the
 * {@link RequirementDenial} of the requirement that decided it and the group's own {@link RequirementGroup#getReason()
 * reason}; {@link me.whereareiam.socialismus.service.requirement.RequirementReasonService} turns them into what the
 * player is told.
 *
 * <pre>{@code
 * RequirementResult result = requirementEvaluator.evaluate(group, player);
 * RequirementReason reason = reasonService.resolve(result, wording, Map.of("chat", chat.getId()));
 * if (reason != null)
 *     player.sendMessage(Serializer.serialize(reason.toContent(player)));
 * }</pre>
 */
@ToString
@EqualsAndHashCode
public final class RequirementResult {
	private static final RequirementResult PASSED = new RequirementResult(true, null, null);
	private static final RequirementResult DENIED = new RequirementResult(false, null, null);

	private final boolean passed;
	private final @Nullable RequirementDenial denial;
	private final @Nullable String reason;

	private RequirementResult(boolean passed, @Nullable RequirementDenial denial, @Nullable String reason) {
		this.passed = passed;
		this.denial = denial;
		this.reason = reason;
	}

	/**
	 * Returns the result of a player who meets the requirement.
	 */
	public static @NotNull RequirementResult passed() {
		return PASSED;
	}

	/**
	 * Returns the result of a player who does not meet the requirement, with nothing to tell them.
	 */
	public static @NotNull RequirementResult denied() {
		return DENIED;
	}

	/**
	 * Returns the result of a player who does not meet a requirement, with its facts.
	 *
	 * @param denial the requirement and how the player failed it, or {@code null} for nothing to tell
	 */
	public static @NotNull RequirementResult denied(@Nullable RequirementDenial denial) {
		return denied(denial, null);
	}

	/**
	 * Returns the result of a player who does not meet a group, with the facts of the requirement that decided it
	 * and the group's own reason.
	 *
	 * @param denial the requirement and how the player failed it, or {@code null} when no requirement can say
	 * @param reason the group's own message template, or {@code null} for none
	 */
	public static @NotNull RequirementResult denied(@Nullable RequirementDenial denial, @Nullable String reason) {
		return denial == null && reason == null ? DENIED : new RequirementResult(false, denial, reason);
	}

	/**
	 * Returns the result of a yes-or-no check, denied with nothing to tell when it fails.
	 *
	 * @param passed whether the player meets the requirement
	 */
	public static @NotNull RequirementResult of(boolean passed) {
		return passed ? PASSED : DENIED;
	}

	/**
	 * Returns whether the player meets the requirement.
	 */
	public boolean isPassed() {
		return passed;
	}

	/**
	 * Returns whether the player does not meet the requirement.
	 */
	public boolean isDenied() {
		return !passed;
	}

	/**
	 * Returns the facts of the requirement that denied the player; {@code null} when the result passed or no
	 * requirement can say.
	 */
	public @Nullable RequirementDenial getDenial() {
		return denial;
	}

	/**
	 * Returns the own reason of the group that denied the player, which is told before any other wording;
	 * {@code null} when the result passed or the group has none.
	 */
	public @Nullable String getReason() {
		return reason;
	}
}
