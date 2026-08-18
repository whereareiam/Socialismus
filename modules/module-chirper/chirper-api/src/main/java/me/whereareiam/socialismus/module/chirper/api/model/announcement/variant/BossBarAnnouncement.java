package me.whereareiam.socialismus.module.chirper.api.model.announcement.variant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.module.chirper.api.model.announcement.AnnouncementContent;
import net.kyori.adventure.bossbar.BossBar;

@Getter
@ToString
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class BossBarAnnouncement extends AnnouncementContent {
    private String message;
    private BossBar.Color color;
    private BossBar.Overlay overlay;
    private BossBarAnnouncementSettings settings;

    @Getter
    @ToString
    @NoArgsConstructor
    @SuperBuilder(toBuilder = true)
    public static class BossBarAnnouncementSettings {
        private boolean breakable;
        private int duration;
    }
}
