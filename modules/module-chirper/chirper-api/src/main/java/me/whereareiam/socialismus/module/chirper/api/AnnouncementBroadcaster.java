package me.whereareiam.socialismus.module.chirper.api;

import me.whereareiam.socialismus.module.chirper.api.model.announcement.Announcement;

public interface AnnouncementBroadcaster {
    void broadcast(Announcement announcement);

    void broadcast(Announcement announcement, boolean simplified);
}
