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
import net.kyori.adventure.bossbar.BossBar;

import java.time.Duration;

/**
 * A boss bar at the top of the tagged player's screen, which empties and goes after a while.
 */
@Getter
@ToString(callSuper = true)
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class BossbarContent extends NotificationContent {
	/**
	 * The bar's text.
	 */
	private String bossbar;

	/**
	 * How the bar looks and how long it stays.
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
	 * How a boss bar looks and how long it stays.
	 */
	@Getter
	@ToString
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Parameters {
		@Builder.Default
		private BossBar.Color color = BossBar.Color.YELLOW;
		@Builder.Default
		private BossBar.Overlay overlay = BossBar.Overlay.PROGRESS;
		@Builder.Default
		private Duration duration = Duration.ofSeconds(5);
	}
}
