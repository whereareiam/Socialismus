package me.whereareiam.socialismus.model.config.message;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.whereareiam.socialismus.type.requirement.RequirementFailure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The wording of one requirement for the two ways a player fails it, as messages files hold it under the
 * requirement's key:
 *
 * <pre>{@code
 * socialismus:world:
 *   required: "This only works in {worlds}."
 *   forbidden: "This does not work in {world}."
 * }</pre>
 *
 * A missing or blank one is not worded here, so the next place in the lookup is asked.
 */
@Getter
@Setter
@ToString
public class RequirementReasons {
	/**
	 * Told to a player who lacks what the requirement asks for.
	 */
	private String required;

	/**
	 * Told to a player who has what the requirement rules out.
	 */
	private String forbidden;

	/**
	 * Returns the wording for a way of failing.
	 *
	 * @param failure how the player failed the requirement
	 * @return the message template, or {@code null} when it is missing or blank
	 */
	public @Nullable String of(@NotNull RequirementFailure failure) {
		String message = failure == RequirementFailure.REQUIRED ? required : forbidden;

		return message == null || message.isBlank() ? null : message;
	}
}
