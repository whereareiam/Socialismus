package me.whereareiam.socialismus.api.model;

import java.util.ArrayList;
import java.util.List;

public class ChatRecipientRequirements {
	public int radius = -1;
	public boolean seeOwnMessage = true;
	public String seePermission;
	public List<String> worlds = new ArrayList<>();
}
