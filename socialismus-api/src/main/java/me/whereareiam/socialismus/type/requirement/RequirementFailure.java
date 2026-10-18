package me.whereareiam.socialismus.type.requirement;

/**
 * How a player fails a requirement, which decides the wording they are told.
 */
public enum RequirementFailure {
	/** The player lacks what the requirement asks for, such as standing in one of its worlds. */
	REQUIRED,

	/** The player has what the requirement rules out with {@code expected: "false"}, such as standing in one of its worlds. */
	FORBIDDEN
}
