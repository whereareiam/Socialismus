package me.whereareiam.socialismus.core.module.chatmention;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.BubbleMessage;
import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import me.whereareiam.socialismus.api.model.chatmention.mention.Mention;
import me.whereareiam.socialismus.core.module.chatmention.mention.MentionFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collection;

@Singleton
public class ChatMentionService {
	private final ChatMentionModule chatMentionModule;
	private final MentionFactory mentionFactory;

	@Inject
	public ChatMentionService(ChatMentionModule chatMentionModule, MentionFactory mentionFactory) {
		this.chatMentionModule = chatMentionModule;
		this.mentionFactory = mentionFactory;
	}

	public ChatMessage hookChatMention(ChatMessage chatMessage, Collection<? extends Player> recipients) {
		if (!chatMessage.getChat().mentions.enabled)
			return chatMessage;

		Component content = chatMessage.getContent();
		Player player = chatMessage.getSender();

		Mention mention = mentionFactory.createMention(player, recipients, content);

		Component component = applyMention(mention);
		chatMessage.setContent(component);

		return chatMessage;
	}

	public BubbleMessage hookChatMention(BubbleMessage bubbleMessage) {
		Player player = bubbleMessage.getSender();
		Component content = bubbleMessage.getContent();

		Mention mention = mentionFactory.createMention(player, bubbleMessage.getRecipients(), content);

		Component component = applyMention(mention);
		bubbleMessage.setContent(component);

		return bubbleMessage;
	}

	private Component applyMention(Mention mention) {
		//TODO Implement formats
		//TODO Implement notifications
		//TODO Implement per player settings

		return mention.getContent();
	}
}
