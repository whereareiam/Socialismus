package me.whereareiam.socialismus.core.module.chatmention;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.BubbleMessage;
import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import me.whereareiam.socialismus.api.model.chatmention.mention.Mention;
import me.whereareiam.socialismus.core.module.chatmention.mention.MentionFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@Singleton
public class ChatMentionService {
	private final MentionFactory mentionFactory;
	private final ChatMentionFormatter chatMentionFormatter;
	private final ChatMentionNotifier chatMentionNotifier;

	@Inject
	public ChatMentionService(MentionFactory mentionFactory, ChatMentionFormatter chatMentionFormatter,
	                          ChatMentionNotifier chatMentionNotifier) {
		this.mentionFactory = mentionFactory;
		this.chatMentionFormatter = chatMentionFormatter;
		this.chatMentionNotifier = chatMentionNotifier;
	}

	public ChatMessage hookChatMention(ChatMessage chatMessage) {
		if (!chatMessage.getChat().mentions.enabled)
			return chatMessage;

		Component content = chatMessage.getContent();
		Player player = chatMessage.getSender();

		Mention mention = mentionFactory.createMention(player, chatMessage.getRecipients(), content);

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
		mention = chatMentionFormatter.formatMention(mention);
		chatMentionNotifier.notifyPlayers(mention);

		return mention.getContent();
	}
}
