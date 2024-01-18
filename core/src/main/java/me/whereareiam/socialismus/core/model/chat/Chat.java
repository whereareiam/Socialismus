package me.whereareiam.socialismus.core.model.chat;

import me.whereareiam.socialismus.core.model.chatmention.ChatMentionSettings;

import java.util.ArrayList;
import java.util.List;

public class Chat {
	public String id;
	public ChatUse usage = new ChatUse();
	public String messageFormat;
	public ChatMentionSettings mentions = new ChatMentionSettings();
	public List<String> hoverFormat = new ArrayList<>();
	public ChatRequirements requirements = new ChatRequirements();
}
