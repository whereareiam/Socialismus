package me.whereareiam.socialismus.api.model.chat;

import me.whereareiam.socialismus.api.model.chatmention.ChatMentionSettings;

import java.util.ArrayList;
import java.util.List;

public class Chat {
	public String id;
	public ChatUse usage = new ChatUse();
	public String messageFormat;
	public boolean enableSwapper = true;
	public ChatMentionSettings mentions = new ChatMentionSettings();
	public List<String> hoverFormat = new ArrayList<>();
	public ChatRequirements requirements = new ChatRequirements();
}
