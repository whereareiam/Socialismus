package me.whereareiam.socialismus.module.bubbler.api.model.bubble;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.configura.annotation.merge.Merge;
import me.whereareiam.configura.type.merge.WhenPresent;
import me.whereareiam.socialismus.module.bubbler.api.model.Vector;
import me.whereareiam.socialismus.module.bubbler.api.type.AlignmentType;
import me.whereareiam.socialismus.module.bubbler.api.type.AnimationType;
import me.whereareiam.socialismus.module.bubbler.api.type.DisplayType;
import me.whereareiam.socialismus.module.bubbler.api.type.TransitionType;

import java.util.List;
import java.util.Map;

/**
 * Represents a chat bubble configuration with display settings, formatting, and styling.
 * Bubbles are rendered above players when they send messages and can be customized
 * through various properties like animations, text styles, and transition effects.
 */
@Getter
@ToString
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Bubble {
	private String id;
	private boolean enabled;
	private int priority;
	private Display display;
	private Format format;

	/**
	 * The styles of {@code styles.yml} whose tags a sender may use in a bubble raised by {@code /socialismus bubble},
	 * by id, from lowest to highest: the sender gets the last one whose requirements they meet. A bubble raised by a
	 * chat message shows the message as its chat read it.
	 */
	@Builder.Default
	@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
	private List<String> styles = List.of();

	private Style style;

	private Map<TransitionType, BubbleTransition> transitions;
	/**
	 * What a sender must meet for this bubble, checked with the parts sender and activation; the first bubble whose
	 * requirements the sender meets is shown.
	 */
	@Builder.Default
	@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
	private Requirements requires = Requirements.none();

	/**
	 * What a nearby player must meet to see the bubble, checked with the parts recipient, sender and activation.
	 */
	@Builder.Default
	@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
	private Requirements reaches = Requirements.none();

	/**
	 * Display configuration controlling visibility and timing of bubbles.
	 */
	@Getter
	@ToString
	@NoArgsConstructor
	@SuperBuilder(toBuilder = true)
	public static class Display {
		private int radius;
		private float headLineGap;
		private float lineSpacing;

		private int maxLinesCount;
		private int maxLineWidth;

		private double timePerSymbol;
		private double minimumTime;
	}

	/**
	 * Format configuration for bubble text content.
	 */
	@Getter
	@ToString
	@NoArgsConstructor
	@SuperBuilder(toBuilder = true)
	public static class Format {
		private String format;
		private String initialFormat;
		private String finalFormat;
		private String queuedFormat;
		private String separatorFormat;
	}

	/**
	 * Visual styling configuration for bubble appearance.
	 */
	@Getter
	@ToString
	@NoArgsConstructor
	@SuperBuilder(toBuilder = true)
	public static class Style {
		private AnimationType animation;
		private DisplayType display;
		private boolean seeThrough;
		private Vector scale;

		private BackgroundStyle background;
		private TextStyle text;

		/**
		 * Background styling for the bubble.
		 */
		@Getter
		@ToString
		@NoArgsConstructor
		@SuperBuilder(toBuilder = true)
		public static class BackgroundStyle {
			private String color;
			private short transparency;

			/**
			 * Parses the hex color string to an integer value.
			 *
			 * @return the color as an integer
			 */
			public int getColor() {
				return Integer.parseInt(
						color.replace("#", ""),
						16
				);
			}
		}

		/**
		 * Text styling for the bubble content.
		 */
		@Getter
		@ToString
		@NoArgsConstructor
		@SuperBuilder(toBuilder = true)
		public static class TextStyle {
			private AlignmentType alignment;
			/** Whether text has a drop shadow */
			private boolean shadow;
		}
	}
}
