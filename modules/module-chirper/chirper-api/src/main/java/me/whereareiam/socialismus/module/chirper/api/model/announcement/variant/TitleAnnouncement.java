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
public class TitleAnnouncement extends AnnouncementContent {
    private String title;
    private String subtitle;
    private TitleAnnouncementSettings settings;

    @Getter
    @ToString
    @NoArgsConstructor
    @SuperBuilder(toBuilder = true)
    public static class TitleAnnouncementSettings {
        private int fadeIn;
        private int stay;
        private int fadeOut;
    }
}
