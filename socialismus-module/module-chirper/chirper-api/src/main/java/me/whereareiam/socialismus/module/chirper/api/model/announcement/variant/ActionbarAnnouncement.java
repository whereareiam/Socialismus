package me.whereareiam.socialismus.module.chirper.api.model.announcement.variant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.module.chirper.api.model.announcement.AnnouncementContent;

@Getter
@ToString
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class ActionbarAnnouncement extends AnnouncementContent {
    private String message;
}
