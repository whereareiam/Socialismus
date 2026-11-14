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
 * A sound the tagged player hears. Where a proxy notifies, the player's server plays it.
 */
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class SoundContent extends NotificationContent {
	/**
	 * The sound's key, such as {@code minecraft:block.note_block.pling}.
	 */
	private String sound;

	/**
	 * How loud and how high it plays.
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
	 * How loud and how high a sound plays.
	 */
	@Getter
	@ToString
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Parameters {
		@Builder.Default
		private float volume = 1.0f;
		@Builder.Default
		private float pitch = 1.0f;
	}
}
