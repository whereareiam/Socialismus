package me.whereareiam.socialismus.model.announcement;

import java.util.ArrayList;
import java.util.List;

public class Announcement {
    public String id;
    public List<String> message = new ArrayList<>();
    public AnnouncementSettings settings = new AnnouncementSettings();
    public AnnouncementRequirements requirements = new AnnouncementRequirements();
}
