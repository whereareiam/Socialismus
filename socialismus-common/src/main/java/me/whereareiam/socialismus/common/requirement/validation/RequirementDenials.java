package me.whereareiam.socialismus.common.requirement.validation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.socialismus.model.requirement.RequirementDenial;
import me.whereareiam.socialismus.model.requirement.RequirementKey;
import me.whereareiam.socialismus.model.requirement.RequirementResult;
import me.whereareiam.socialismus.type.requirement.RequirementFailure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Builds the denials of the built-in requirements, which name what a requirement lists the same way everywhere.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class RequirementDenials {
	/**
	 * Denies a player with the facts of how they failed.
	 *
	 * @param key          the requirement's key
	 * @param held         whether the player has what the requirement names, so it was ruled out rather than missing
	 * @param placeholders the requirement's placeholders
	 */
	static @NotNull RequirementResult deny(
			@NotNull RequirementKey<?> key,
			boolean held,
			@NotNull Map<String, String> placeholders
	) {
		RequirementFailure failure = held ? RequirementFailure.FORBIDDEN : RequirementFailure.REQUIRED;

		return RequirementResult.denied(RequirementDenial.of(key, failure, placeholders));
	}

	/**
	 * Lists what a requirement names for a placeholder, such as its worlds.
	 */
	static @NotNull String listed(@Nullable List<String> values) {
		return values == null ? "" : String.join(", ", values);
	}
}
