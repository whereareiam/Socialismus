package me.whereareiam.socialismus.core.module.chatmention;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.BubbleMessage;
import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@Singleton
public class ChatMentionService {
	private final ChatMentionModule chatMentionModule;

	@Inject
	public ChatMentionService(ChatMentionModule chatMentionModule) {
		this.chatMentionModule = chatMentionModule;
	}

	public ChatMessage hookChatMention(ChatMessage chatMessage) {
		//TODO Implement per chat settings

		Component component = hookChatMention(chatMessage.getSender(), chatMessage.getContent());
		chatMessage.setContent(component);

		return chatMessage;
	}

	public BubbleMessage hookChatMention(BubbleMessage bubbleMessage) {
		Component component = hookChatMention(bubbleMessage.getSender(), bubbleMessage.getContent());
		bubbleMessage.setContent(component);

		return bubbleMessage;
	}

	private Component hookChatMention(Player player, Component component) {
		//TODO Implement formats
		//TODO Implement notifications
		//TODO Implement per player settings

		return component;
	}
}
