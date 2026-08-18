package me.whereareiam.socialismus.module.chirper.api.model.announcement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
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
    private RequirementGroup requirements;

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
