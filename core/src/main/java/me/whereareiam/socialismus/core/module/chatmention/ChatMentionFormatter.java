package me.whereareiam.socialismus.core.module.chatmention;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.model.chatmention.ChatMentionFormat;
import me.whereareiam.socialismus.api.model.chatmention.mention.Mention;
import me.whereareiam.socialismus.core.util.FormatterUtil;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import me.whereareiam.socialismus.core.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.entity.Player;

@Singleton
public class ChatMentionFormatter {
	private final LoggerUtil loggerUtil;
	private final FormatterUtil formatterUtil;
	private final MessageUtil messageUtil;
	private final ChatMentionModule chatMentionModule;

	@Inject
	public ChatMentionFormatter(LoggerUtil loggerUtil, FormatterUtil formatterUtil, MessageUtil messageUtil,
	                            ChatMentionModule chatMentionModule) {
		this.loggerUtil = loggerUtil;
		this.formatterUtil = formatterUtil;
		this.chatMentionModule = chatMentionModule;
		this.messageUtil = messageUtil;
	}

	public Mention formatMention(Mention mention) {
		ChatMentionFormat format = chatMentionModule.getFormats().stream().filter(
				f -> mention.getSender().hasPermission(f.permission)
		).findFirst().orElse(null);

		if (format == null)
			return mention;

		loggerUtil.debug("Formatting mention: " + mention + ", format: " + format);
		Component content = mention.getContent();

		for (Player player : mention.getMentionedPlayers()) {
			loggerUtil.trace("Formatting mention for player: " + player.getName());
			Component formatComponent = formatterUtil.formatMessage(mention.getSender(), format.format);
			formatComponent = messageUtil.replacePlaceholder(formatComponent, "{playerName}", player.getName());

			Component hoverComponent;
			if (!format.hoverFormat.isEmpty()) {
				loggerUtil.trace("Hover format is not empty, applying hover event.");
				hoverComponent = formatterUtil.formatMessage(mention.getSender(), String.join("\n", format.hoverFormat));
				hoverComponent = messageUtil.replacePlaceholder(hoverComponent, "{playerName}", player.getName());

				content = messageUtil.replacePlaceholder(content, player.getName(), formatComponent.hoverEvent(HoverEvent.showText(hoverComponent)));
			} else {
				loggerUtil.trace("Hover format is empty, not applying hover event.");
				content = messageUtil.replacePlaceholder(content, player.getName(), formatComponent);
			}
		}

		mention.setContent(content);

		return mention;
	}
}
