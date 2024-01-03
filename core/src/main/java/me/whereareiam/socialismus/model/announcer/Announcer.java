package me.whereareiam.socialismus.model.announcer;

import me.whereareiam.socialismus.module.announcer.AnnouncementSelectionType;

import java.util.ArrayList;
import java.util.List;

public class Announcer {
    public boolean enabled = true;
    public int interval = 60;
    public AnnouncementSelectionType selectionType = AnnouncementSelectionType.SEQUENTIAL;
    public List<String> announcements = new ArrayList<>();
}
