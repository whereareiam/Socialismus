package me.whereareiam.socialismus.model.announcer;

import java.util.List;

public class Announcer {
    public String id;
    public boolean enabled;
    public List<String> message;
    public AnnouncerSettings settings = new AnnouncerSettings();
    public AnnouncerRequirements requirements = new AnnouncerRequirements();
}
