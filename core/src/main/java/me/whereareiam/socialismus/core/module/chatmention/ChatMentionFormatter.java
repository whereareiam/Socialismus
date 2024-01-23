package me.whereareiam.socialismus.core.module.chatmention;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.chatmention.ChatMentionFormat;
import me.whereareiam.socialismus.api.model.chatmention.mention.Mention;
import me.whereareiam.socialismus.core.config.module.chatmention.ChatMentionConfig;
import me.whereareiam.socialismus.core.util.FormatterUtil;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import me.whereareiam.socialismus.core.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;

import java.util.Optional;

@Singleton
public class ChatMentionFormatter {
	private final FormatterUtil formatterUtil;
	private final MessageUtil messageUtil;
	private final ChatMentionConfig chatMentionConfig;
	private final ChatMentionModule chatMentionModule;

	@Inject
	public ChatMentionFormatter(LoggerUtil loggerUtil, FormatterUtil formatterUtil, MessageUtil messageUtil,
	                            ChatMentionConfig chatMentionConfig, ChatMentionModule chatMentionModule) {
		this.formatterUtil = formatterUtil;
		this.chatMentionConfig = chatMentionConfig;
		this.chatMentionModule = chatMentionModule;
		this.messageUtil = messageUtil;

		loggerUtil.trace("Initializing class: " + this);
	}

	public Mention formatMention(Mention mention) {
		if (mention.getUsedAllTag() != null)
			return formatAllMention(mention);
		else if (mention.getUsedNearbyTag() != null)
			return formatNearbyMention(mention);
		else
			return formatPlayerMention(mention);
	}

	private Mention formatAllMention(Mention mention) {
		String tag = mention.getUsedAllTag();
		String format = chatMentionConfig.settings.allTagSettings.format
				.replace("{usedTag}", tag);

		Component component = formatterUtil.formatMessage(mention.getSender(), format);
		mention.setContent(messageUtil.replacePlaceholder(mention.getContent(), tag, component));

		return mention;
	}

	private Mention formatNearbyMention(Mention mention) {
		String tag = mention.getUsedNearbyTag();
		String format = chatMentionConfig.settings.nearbyTagSettings.format
				.replace("{usedTag}", tag);

		Component component = formatterUtil.formatMessage(mention.getSender(), format);
		mention.setContent(messageUtil.replacePlaceholder(mention.getContent(), tag, component));

		return mention;
	}

	private Mention formatPlayerMention(Mention mention) {
		Optional<ChatMentionFormat> format = chatMentionModule.getFormats().stream().filter(
				f -> f.permission.isBlank() || f.permission.isEmpty() || mention.getSender().hasPermission(f.permission)
		).findFirst();

		if (format.isEmpty())
			return mention;

		Component content = mention.getContent();

		for (Player player : mention.getMentionedPlayers()) {
			Component formatComponent = formatterUtil.formatMessage(mention.getSender(), format.get().format);
			formatComponent = messageUtil.replacePlaceholder(formatComponent, "{playerName}", player.getName());

			Component hoverComponent;
			if (!format.get().hoverFormat.isEmpty()) {
				hoverComponent = formatterUtil.formatMessage(mention.getSender(), String.join("\n", format.get().hoverFormat));
				hoverComponent = messageUtil.replacePlaceholder(hoverComponent, "{playerName}", player.getName());

				content = messageUtil.replacePlaceholder(content, player.getName(), formatComponent.hoverEvent(HoverEvent.showText(hoverComponent)));
			} else {
				content = messageUtil.replacePlaceholder(content, player.getName(), formatComponent);
			}
		}

		mention.setContent(content);

		return mention;
	}
}
