package me.whereareiam.socialismus.module.bubbler.api.model.bubble;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Represents a transition effect that occurs during bubble lifecycle phases.
 * Transitions can include sound effects and other feedback.
 */
@Getter
@ToString
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class BubbleTransition {
	private Sound sound;

	/**
	 * Sound effect configuration for bubble transitions.
	 */
	@Getter
	@ToString
	@NoArgsConstructor
	@SuperBuilder(toBuilder = true)
	public static class Sound {
		private String type;
		private float volume;
		private float pitch;
		private boolean fade;
	}
}
