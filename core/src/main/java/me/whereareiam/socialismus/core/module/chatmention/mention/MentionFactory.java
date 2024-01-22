package me.whereareiam.socialismus.core.module.chatmention.mention;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.BubbleMessage;
import me.whereareiam.socialismus.api.model.chat.Chat;
import me.whereareiam.socialismus.api.model.chat.ChatMessage;
import me.whereareiam.socialismus.api.model.chatmention.mention.Mention;
import me.whereareiam.socialismus.core.config.module.bubblechat.BubbleChatConfig;
import me.whereareiam.socialismus.core.config.module.chatmention.ChatMentionConfig;
import me.whereareiam.socialismus.core.util.DistanceCalculatorUtil;
import me.whereareiam.socialismus.core.util.LoggerUtil;
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
	private final LoggerUtil loggerUtil;
	private final ChatMentionConfig chatMentionConfig;
	private final BubbleChatConfig bubbleChatConfig;

	@Inject
	public MentionFactory(LoggerUtil loggerUtil, ChatMentionConfig chatMentionConfig,
	                      BubbleChatConfig bubbleChatConfig) {
		this.loggerUtil = loggerUtil;
		this.chatMentionConfig = chatMentionConfig;
		this.bubbleChatConfig = bubbleChatConfig;

		loggerUtil.trace("Initializing class: " + this);
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

		String usedNearbyTag = chatMentionConfig.settings.nearbyTagSettings.tags.stream()
				.filter(plainContent::contains)
				.findFirst()
				.orElse(null);

		Collection<? extends Player> recipients = getRecipients(sender, players, plainContent, usedAllTag, usedNearbyTag, optionalChat);

		Mention mention = new Mention(content, usedAllTag, usedNearbyTag, sender, recipients);

		loggerUtil.debug(" "
				+ "\n Created new mention"
				+ "\n content: " + PlainTextComponentSerializer.plainText().serialize(content)
				+ "\n recipients: " + recipients.stream().map(Player::getName).toList()
				+ "\n sender: " + sender.getName()
				+ "\n usedNearbyTag: " + usedNearbyTag
				+ "\n usedAllTag: " + usedAllTag
				+ " "
		);

		return mention;
	}

	private Collection<? extends Player> getRecipients(Player sender, Collection<? extends Player> players, List<String> plainContent, String usedAllTag, String usedNearbyTag, Optional<Chat> optionalChat) {
		Chat chat = optionalChat.orElse(null);

		if (usedAllTag != null && sender.hasPermission(chatMentionConfig.settings.allTagSettings.permission)) {
			Collection<? extends Player> recipients = Bukkit.getOnlinePlayers();
			if (!sender.hasPermission(chatMentionConfig.settings.selfMentionPermission))
				recipients.remove(sender);

			return recipients;
		}

		if (usedNearbyTag != null && sender.hasPermission(chatMentionConfig.settings.nearbyTagSettings.permission)) {
			if (!sender.hasPermission(chatMentionConfig.settings.selfMentionPermission))
				players.remove(sender);

			return players;
		}

		Collection<? extends Player> recipients = players.stream()
				.filter(p -> plainContent.stream().anyMatch(s -> s.contains(p.getName())))
				.toList();

		if (chat != null) {
			int chatRadius = chat.requirements.recipient.radius;
			int mentionRadius = chat.mentions.radius;

			if (mentionRadius < chatRadius) {
				recipients = recipients.stream()
						.filter(p -> DistanceCalculatorUtil.calculateDistance(p, sender) <= mentionRadius)
						.toList();
			} else if (mentionRadius > chatRadius) {
				recipients = Bukkit.getOnlinePlayers().stream()
						.filter(p -> DistanceCalculatorUtil.calculateDistance(p, sender) <= mentionRadius)
						.toList();
			}

			if (recipients.size() > chat.mentions.maxMentions) {
				int maxMentions = getMaxMentions(chat, sender);
				recipients = recipients.stream().limit(maxMentions).toList();
			}
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
