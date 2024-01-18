package me.whereareiam.socialismus.core.module.chatmention;

import com.google.inject.Singleton;
import net.kyori.adventure.text.Component;

@Singleton
public class ChatMentionService {
	private final ChatMentionModule chatMentionModule;

	public ChatMentionService(ChatMentionModule chatMentionModule) {this.chatMentionModule = chatMentionModule;}

	public Component hookChatMention(Component message) {


		return message;
	}
}
