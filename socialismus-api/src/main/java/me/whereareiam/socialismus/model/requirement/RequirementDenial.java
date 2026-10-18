package me.whereareiam.socialismus.model.requirement;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.whereareiam.socialismus.type.requirement.RequirementFailure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * The facts of a requirement a player does not meet: which requirement, how the player failed it, and placeholders
 * that mean the same wherever the requirement is used, such as {@code {worlds}} and {@code {world}}. It carries no
 * wording of its own: the feature that checked the requirement words it, through
 * {@link me.whereareiam.socialismus.service.requirement.RequirementReasonService}.
 *
 * <p>A module's requirement may add a default message, told when neither the feature nor {@code messages.yml}
 * words its key.</p>
 *
 * <pre>{@code
 * RequirementDenial denial = RequirementDenial.of(MUTED, RequirementFailure.FORBIDDEN, Map.of("until", "12:30"))
 *         .withMessage("<red>You are muted until {until}.");
 * }</pre>
 */
@Getter
@ToString
@EqualsAndHashCode
public final class RequirementDenial {
	/**
	 * The full key of the requirement, such as {@code socialismus:world}; the wording is looked up by it.
	 */
	private final @NotNull String key;

	/**
	 * How the player failed the requirement.
	 */
	private final @NotNull RequirementFailure failure;

	/**
	 * The values of the requirement's placeholders, by their key without braces.
	 */
	private final @NotNull Map<String, String> placeholders;

	/**
	 * The requirement's own wording, told when nothing else words it; {@code null} for none.
	 */
	private final @Nullable String message;

	private RequirementDenial(
			@NotNull String key,
			@NotNull RequirementFailure failure,
			@NotNull Map<String, String> placeholders,
			@Nullable String message
	) {
		this.key = key;
		this.failure = failure;
		this.placeholders = Map.copyOf(placeholders);
		this.message = message;
	}

	/**
	 * Creates the denial of a requirement.
	 *
	 * @param key          the key the requirement's validation is registered under
	 * @param failure      how the player failed it
	 * @param placeholders the values of its placeholders, by their key without braces
	 * @return the denial, without a message of its own
	 */
	public static @NotNull RequirementDenial of(
			@NotNull RequirementKey<?> key,
			@NotNull RequirementFailure failure,
			@NotNull Map<String, String> placeholders
	) {
		return new RequirementDenial(key.getFullKey(), failure, placeholders, null);
	}

	/**
	 * Returns this denial with the requirement's own wording, told when neither the feature nor {@code messages.yml}
	 * words its key.
	 *
	 * @param message the message template, with this denial's placeholders
	 * @return the denial with the message
	 */
	public @NotNull RequirementDenial withMessage(@NotNull String message) {
		return new RequirementDenial(key, failure, placeholders, message);
	}
}
