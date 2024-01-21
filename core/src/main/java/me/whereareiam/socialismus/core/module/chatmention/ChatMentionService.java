package me.whereareiam.socialismus.core.module.chatmention;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.BubbleMessage;
import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import me.whereareiam.socialismus.api.model.chatmention.mention.Mention;
import me.whereareiam.socialismus.core.module.chatmention.mention.MentionFactory;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import net.kyori.adventure.text.Component;

@Singleton
public class ChatMentionService {
	private final LoggerUtil loggerUtil;
	private final MentionFactory mentionFactory;
	private final ChatMentionFormatter chatMentionFormatter;
	private final ChatMentionNotifier chatMentionNotifier;

	@Inject
	public ChatMentionService(LoggerUtil loggerUtil, MentionFactory mentionFactory,
	                          ChatMentionFormatter chatMentionFormatter, ChatMentionNotifier chatMentionNotifier) {
		this.loggerUtil = loggerUtil;
		this.mentionFactory = mentionFactory;
		this.chatMentionFormatter = chatMentionFormatter;
		this.chatMentionNotifier = chatMentionNotifier;
	}

	public ChatMessage hookChatMention(ChatMessage chatMessage) {
		loggerUtil.debug("Hooking chat mention for chat message: " + chatMessage);
		if (!chatMessage.getChat().mentions.enabled)
			return chatMessage;

		Mention mention = mentionFactory.createMention(chatMessage);

		Component component = applyMention(mention);
		chatMessage.setContent(component);

		return chatMessage;
	}

	public BubbleMessage hookChatMention(BubbleMessage bubbleMessage) {
		loggerUtil.debug("Hooking chat mention for bubble message: " + bubbleMessage);
		Mention mention = mentionFactory.createMention(bubbleMessage);

		Component component = applyMention(mention);
		bubbleMessage.setContent(component);

		return bubbleMessage;
	}

	private Component applyMention(Mention mention) {
		if (mention.getMentionedPlayers().isEmpty())
			return mention.getContent();

		loggerUtil.debug("Applying mention: " + mention);
		mention = chatMentionFormatter.formatMention(mention);
		loggerUtil.debug("Notifying players: " + mention.getMentionedPlayers());
		chatMentionNotifier.notifyPlayers(mention);

		return mention.getContent();
	}
}
