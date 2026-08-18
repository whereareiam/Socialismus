package me.whereareiam.socialismus.module.bubbler.api.type;

/**
 * Defines the animation styles for bubble appearance and disappearance.
 */
public enum AnimationType {
	/** Bubble contracts/shrinks when appearing or disappearing */
	CONTRACTION,
	/** Bubble expands/grows when appearing or disappearing */
	EXPANSION,
	/** Bubble pops out with a bounce effect */
	POPOUT,
	/** No animation, bubble appears/disappears instantly */
	STATIC
}
