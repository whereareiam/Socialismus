package me.whereareiam.socialismus.module.chirper.api.model.announcement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.configura.annotation.merge.Merge;
import me.whereareiam.configura.feature.polymorphic.api.annotation.Polymorphic;
import me.whereareiam.configura.type.merge.WhenPresent;
import me.whereareiam.socialismus.module.chirper.api.model.announcement.variant.*;
import org.jetbrains.annotations.Nullable;

/**
 * Base polymorphic payload for a single announcement output variant.
 * Concrete subtypes are inferred from their distinctive fields during configuration loading.
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Polymorphic(
		inferBy = {
				@Polymorphic.Infer(field = "overlay", target = BossBarAnnouncement.class),
				@Polymorphic.Infer(field = "subtitle", target = TitleAnnouncement.class),
				@Polymorphic.Infer(field = "sound", target = SoundAnnouncement.class),
				@Polymorphic.Infer(field = "messages", target = MessageAnnouncement.class)
		},
		defaultTarget = ActionbarAnnouncement.class
)
public class AnnouncementContent {
	/**
	 * What a player must meet to receive this content, checked with the part recipient; everyone when absent.
	 */
	@Merge(present = WhenPresent.KEEP_AS_WRITTEN)
	private @Nullable Requirements requires;
}
