package me.whereareiam.socialismus.api.model.announcer;

import me.whereareiam.socialismus.api.type.AnnouncementSelectionType;

import java.util.ArrayList;
import java.util.List;

public class Announcer {
	public boolean enabled = true;
	public int interval = 60;
	public AnnouncementSelectionType selectionType = AnnouncementSelectionType.SEQUENTIAL;
	public List<String> announcements = new ArrayList<>();
}
