package me.whereareiam.socialismus.core.config.module.chatmention;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.chatmention.ChatMentionFormat;
import me.whereareiam.socialismus.api.model.chatmention.ChatMentionNotificationFormat;
import net.elytrium.serializer.language.object.YamlSerializable;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class ChatMentionConfig extends YamlSerializable {
	public List<ChatMentionFormat> formats = new ArrayList<>();
	public List<ChatMentionNotificationFormat> notifications = new ArrayList<>();
	public ChatMentionSettingsConfig settings = new ChatMentionSettingsConfig();
}
