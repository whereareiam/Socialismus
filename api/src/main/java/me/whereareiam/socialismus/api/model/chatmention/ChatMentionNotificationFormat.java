package me.whereareiam.socialismus.api.model.chatmention;

import me.whereareiam.socialismus.api.model.chatmention.notification.*;
import me.whereareiam.socialismus.api.type.ChatMentionNotificationType;

import java.util.List;

public class ChatMentionNotificationFormat {
	public boolean enabled = true;
	public String permission = "";
	public List<ChatMentionNotificationType> types = List.of(ChatMentionNotificationType.TITLE, ChatMentionNotificationType.SOUND);
	public ActionBarNotification actionBar = new ActionBarNotification();
	public BossBarNotification bossBar = new BossBarNotification();
	public ChatNotification chat = new ChatNotification();
	public SoundNotification sound = new SoundNotification();
	public TitleNotification title = new TitleNotification();
}
