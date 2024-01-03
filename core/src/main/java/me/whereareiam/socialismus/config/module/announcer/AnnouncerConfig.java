package me.whereareiam.socialismus.config.module.announcer;

import me.whereareiam.socialismus.model.announcement.Announcement;
import net.elytrium.serializer.language.object.YamlSerializable;

import java.util.ArrayList;
import java.util.List;

public class AnnouncerConfig extends YamlSerializable {
    public List<Announcement> announcements = new ArrayList<>();
}
