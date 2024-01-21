package me.whereareiam.socialismus.core.module.chatmention;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.chatmention.ChatMentionFormat;
import me.whereareiam.socialismus.api.model.chatmention.mention.Mention;
import me.whereareiam.socialismus.core.config.module.chatmention.ChatMentionConfig;
import me.whereareiam.socialismus.core.util.FormatterUtil;
import me.whereareiam.socialismus.core.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;

@Singleton
public class ChatMentionFormatter {
	private final FormatterUtil formatterUtil;
	private final MessageUtil messageUtil;
	private final ChatMentionConfig chatMentionConfig;
	private final ChatMentionModule chatMentionModule;

	@Inject
	public ChatMentionFormatter(FormatterUtil formatterUtil, MessageUtil messageUtil,
	                            ChatMentionConfig chatMentionConfig, ChatMentionModule chatMentionModule) {
		this.formatterUtil = formatterUtil;
		this.chatMentionConfig = chatMentionConfig;
		this.chatMentionModule = chatMentionModule;
		this.messageUtil = messageUtil;
	}

	public Mention formatMention(Mention mention) {
		if (mention.canMentionAll())
			return formatAllMention(mention);
		else if (mention.canMentionNearby())
			return formatNearbyMention(mention);
		else
			return formatPlayerMention(mention);
	}

	private Mention formatAllMention(Mention mention) {
		Component component = formatterUtil.formatMessage(mention.getSender(), chatMentionConfig.settings.allFormat);
		mention.setContent(messageUtil.replacePlaceholder(mention.getContent(), "@all", component));
		
		return mention;
	}

	private Mention formatNearbyMention(Mention mention) {
		Component component = formatterUtil.formatMessage(mention.getSender(), chatMentionConfig.settings.nearbyFormat);
		mention.setContent(messageUtil.replacePlaceholder(mention.getContent(), "@nearby", component));

		return mention;
	}

	private Mention formatPlayerMention(Mention mention) {
		ChatMentionFormat format = chatMentionModule.getFormats().stream().filter(
				f -> mention.getSender().hasPermission(f.permission)
		).findFirst().orElse(null);

		if (format == null)
			return mention;

		Component content = mention.getContent();

		for (Player player : mention.getMentionedPlayers()) {
			Component formatComponent = formatterUtil.formatMessage(mention.getSender(), format.format);
			formatComponent = messageUtil.replacePlaceholder(formatComponent, "{playerName}", player.getName());

			Component hoverComponent;
			if (!format.hoverFormat.isEmpty()) {
				hoverComponent = formatterUtil.formatMessage(mention.getSender(), String.join("\n", format.hoverFormat));
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
