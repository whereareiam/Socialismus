package me.whereareiam.socialismus.core.util;

import co.aikar.commands.CommandIssuer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.core.platform.PlatformMessageSender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.entity.Player;

@Singleton
public class MessageUtil {
	private final LoggerUtil loggerUtil;
	private final FormatterUtil formatterUtil;
	private final PlatformMessageSender platformMessageSender;

	@Inject
	public MessageUtil(LoggerUtil loggerUtil, FormatterUtil formatterUtil, PlatformMessageSender platformMessageSender) {
		this.loggerUtil = loggerUtil;
		this.formatterUtil = formatterUtil;
		this.platformMessageSender = platformMessageSender;

		loggerUtil.trace("Initializing class: " + this);
	}

	public void sendMessage(CommandIssuer issuer, String message) {
		if (issuer.isPlayer()) {
			sendMessage(issuer.getIssuer(), formatterUtil.formatMessage(issuer.getIssuer(), message));
		} else {
			issuer.sendMessage(formatterUtil.cleanMessage(message));
		}
	}

	public void sendMessage(Player sender, String message) {
		sendMessage(sender, formatterUtil.formatMessage(sender, message));
	}

	public void sendMessage(Player sender, Component message) {
		loggerUtil.debug("Sending message to " + sender.getName());
		platformMessageSender.sendMessage(sender, message);
	}

	public Component replacePlaceholder(Component component, String placeholder, Object content) {
		TextReplacementConfig textReplacementConfig;
		if (content instanceof String) {
			textReplacementConfig = TextReplacementConfig.builder()
					.matchLiteral(placeholder)
					.replacement((String)content)
					.build();
		} else {
			textReplacementConfig = TextReplacementConfig.builder()
					.matchLiteral(placeholder)
					.replacement((Component)content)
					.build();
		}

		return component.replaceText(textReplacementConfig);
	}
}
