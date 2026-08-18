package me.whereareiam.socialismus.module.chirper.api.model.announcement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.configura.feature.polymorphic.api.annotation.Polymorphic;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
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
	private @Nullable RequirementGroup requirements;
}
