package me.whereareiam.socialismus.module.mentionator.api.model.notification.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.configura.annotation.merge.Merge;
import me.whereareiam.configura.type.merge.WhenPresent;
import me.whereareiam.socialismus.module.mentionator.api.model.notification.NotificationContent;
import org.jetbrains.annotations.Nullable;

/**
 * A title in the middle of the tagged player's screen, gold unless its text says otherwise, with a subtitle below it,
 * dark gray unless its text says otherwise.
 */
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class TitleContent extends NotificationContent {
	/**
	 * The title.
	 */
	private String title;

	/**
	 * The subtitle; none when absent.
	 */
	private @Nullable String subtitle;

	/**
	 * How long the title fades in, stays and fades out.
	 */
	@lombok.Builder.Default
	private Parameters parameters = new Parameters();

	/**
	 * What the tagged player must meet to get this content, checked with the parts target and sender; everyone when
	 * absent.
	 */
	@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
	private @Nullable Requirements requires;

	/**
	 * How long a title fades in, stays and fades out, in ticks.
	 */
	@Getter
	@ToString
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Parameters {
		@Builder.Default
		private int fadeIn = 10;
		@Builder.Default
		private int stay = 40;
		@Builder.Default
		private int fadeOut = 10;
	}
}
