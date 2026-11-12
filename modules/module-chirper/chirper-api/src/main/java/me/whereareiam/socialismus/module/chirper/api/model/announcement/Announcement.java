package me.whereareiam.socialismus.module.chirper.api.model.announcement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.conditia.model.Requirements;
import me.whereareiam.configura.annotation.merge.Merge;
import me.whereareiam.configura.type.merge.WhenPresent;
import me.whereareiam.socialismus.module.chirper.api.type.AnnouncementType;

import java.util.Map;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class Announcement {
    public boolean enabled;
    private String id;
    private AnnouncementSettings settings;
    private Map<AnnouncementType, ? extends AnnouncementContent> contents;
    /**
     * What a player must meet to receive the announcement, checked with the part recipient.
     */
    @Merge(present = WhenPresent.KEEP_AS_WRITTEN)
    private Requirements requires;

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder(toBuilder = true)
    public static class AnnouncementSettings {
        public int delay;
        public boolean repeat;
    }
}
