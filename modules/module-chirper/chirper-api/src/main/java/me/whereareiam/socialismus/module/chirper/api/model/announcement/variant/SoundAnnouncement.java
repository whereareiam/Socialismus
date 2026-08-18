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
public class SoundAnnouncement extends AnnouncementContent {
    private String sound;
    private SoundAnnouncementSettings settings;

    @Getter
    @ToString
    @NoArgsConstructor
    @SuperBuilder(toBuilder = true)
    public static class SoundAnnouncementSettings {
        private float volume;
        private float pitch;
    }
}
