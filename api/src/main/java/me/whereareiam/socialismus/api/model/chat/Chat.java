package me.whereareiam.socialismus.api.model.chat;

import me.whereareiam.socialismus.api.model.chatmention.ChatMentionSettings;

import java.util.ArrayList;
import java.util.List;

public class Chat {
	public String id;
	public ChatUse usage = new ChatUse();
	public List<ChatMessageFormat> formats = new ArrayList<>();
	public boolean enableSwapper = true;
	public ChatMentionSettings mentions = new ChatMentionSettings();
	public ChatRequirements requirements = new ChatRequirements();
}
