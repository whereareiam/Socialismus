package me.whereareiam.socialismus.core.module.chatmention.mention;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.BubbleMessage;
import me.whereareiam.socialismus.api.model.chat.Chat;
import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import me.whereareiam.socialismus.api.model.chatmention.mention.Mention;
import me.whereareiam.socialismus.core.config.module.bubblechat.BubbleChatConfig;
import me.whereareiam.socialismus.core.config.module.chatmention.ChatMentionConfig;
import me.whereareiam.socialismus.core.util.FormatterUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Singleton
public class MentionFactory {
	private final FormatterUtil formatterUtil;
	private final ChatMentionConfig chatMentionConfig;
	private final BubbleChatConfig bubbleChatConfig;

	@Inject
	public MentionFactory(FormatterUtil formatterUtil, ChatMentionConfig chatMentionConfig,
	                      BubbleChatConfig bubbleChatConfig) {
		this.formatterUtil = formatterUtil;
		this.chatMentionConfig = chatMentionConfig;
		this.bubbleChatConfig = bubbleChatConfig;
	}

	public Mention createMention(BubbleMessage bubbleMessage) {
		return createMention(bubbleMessage.getSender(), bubbleMessage.getRecipients(), bubbleMessage.getContent(), Optional.empty());
	}

	public Mention createMention(ChatMessage chatMessage) {
		return createMention(chatMessage.getSender(), chatMessage.getRecipients(), chatMessage.getContent(), Optional.of(chatMessage.getChat()));
	}

	private Mention createMention(Player sender, Collection<? extends Player> players, Component content, Optional<Chat> optionalChat) {
		List<String> plainContent = List.of(PlainTextComponentSerializer.plainText().serialize(content).split(" "));

		String usedAllTag = chatMentionConfig.settings.allTagSettings.tags.stream()
				.filter(plainContent::contains)
				.findFirst()
				.orElse(null);

		String usedNearbyTag = chatMentionConfig.settings.allTagSettings.tags.stream()
				.filter(plainContent::contains)
				.findFirst()
				.orElse(null);

		Collection<? extends Player> recipients = getRecipients(sender, players, plainContent, usedAllTag, usedNearbyTag, optionalChat);

		return new Mention(content, usedAllTag, usedNearbyTag, sender, recipients);
	}

	private Collection<? extends Player> getRecipients(Player sender, Collection<? extends Player> players, List<String> plainContent, String usedAllTag, String usedNearbyTag, Optional<Chat> optionalChat) {
		Chat chat = optionalChat.orElse(null);

		if (usedAllTag != null && sender.hasPermission(chatMentionConfig.settings.allTagSettings.permission)) {
			return Bukkit.getOnlinePlayers();
		}

		if (usedNearbyTag != null && sender.hasPermission(chatMentionConfig.settings.nearbyTagSettings.permission)) {
			return players;
		}

		Collection<? extends Player> recipients = players.stream()
				.filter(p -> plainContent.stream().anyMatch(s -> s.contains(p.getName())))
				.toList();

		if (chat != null && recipients.size() > chat.mentions.maxMentions) {
			int maxMentions = getMaxMentions(chat, sender);
			recipients = recipients.stream().limit(maxMentions).toList();
		}

		if (chat == null) {
			recipients = recipients.stream()
					.limit(bubbleChatConfig.settings.maxMentions)
					.filter(p -> !p.equals(sender))
					.toList();
		}

		return recipients;

	}

	private int getMaxMentions(Chat chat, Player player) {
		int maxMentions = chat.mentions.maxMentions;
		String prefix = chatMentionConfig.settings.maxMentionPermission;

		return player.getEffectivePermissions().stream()
				.map(PermissionAttachmentInfo::getPermission)
				.filter(perm -> perm.startsWith(prefix))
				.map(perm -> perm.substring(prefix.length()))
				.mapToInt(Integer::parseInt)
				.max()
				.orElse(maxMentions);
	}
}
