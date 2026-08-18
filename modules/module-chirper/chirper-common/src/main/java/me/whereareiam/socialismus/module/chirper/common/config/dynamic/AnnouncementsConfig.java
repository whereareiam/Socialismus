package me.whereareiam.socialismus.module.chirper.common.config.dynamic;

import lombok.Getter;
import me.whereareiam.socialismus.module.chirper.api.model.announcement.Announcement;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AnnouncementsConfig {
    private List<Announcement> announcements = new ArrayList<>();
}
