package me.whereareiam.socialismus.api.model;

import java.util.ArrayList;
import java.util.List;

public class Chat {
	public String id;
	public ChatUse usage = new ChatUse();
	public List<ChatMessageFormat> formats = new ArrayList<>();
	public ChatSettings settings = new ChatSettings();
	public ChatRequirements requirements = new ChatRequirements();
}
