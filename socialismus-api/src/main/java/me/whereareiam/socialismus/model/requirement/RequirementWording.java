package me.whereareiam.socialismus.model.requirement;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * The words a line of a {@code requires:} list may carry a text under, beside its condition. A failure takes the
 * text of its own line, or of the nearest line around it that has one; a text may refer to phrases as
 * {@code <ref:key>}.
 *
 * <pre>{@code
 * requires:
 *   - permission: socialismus.chat.staff
 *     denied: "<ref:staff-only>"
 *     hint: "<gray>Ask a moderator for access."
 * }</pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequirementWording {
	/**
	 * What the player is told when the line denies them, instead of the {@code denied.*} phrase of the reason.
	 */
	public static final String DENIED = "denied";

	/**
	 * How the player can meet the line, told after why it denied them.
	 */
	public static final String HINT = "hint";
}
