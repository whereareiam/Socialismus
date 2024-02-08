package me.whereareiam.socialismus.core.util;

import co.aikar.commands.CommandIssuer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.core.Scheduler;
import me.whereareiam.socialismus.core.platform.PlatformMessageSender;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Singleton
public class MessageUtil {
	private final Scheduler scheduler;
	private final LoggerUtil loggerUtil;
	private final FormatterUtil formatterUtil;
	private final PlatformMessageSender platformMessageSender;

	@Inject
	public MessageUtil(Scheduler scheduler, LoggerUtil loggerUtil, FormatterUtil formatterUtil,
	                   PlatformMessageSender platformMessageSender) {
		this.scheduler = scheduler;
		this.loggerUtil = loggerUtil;
		this.formatterUtil = formatterUtil;
		this.platformMessageSender = platformMessageSender;

		loggerUtil.trace("Initializing class: " + this);
	}

	public void sendMessage(CommandIssuer issuer, String message) {
		if (message == null || message.isEmpty())
			return;

		if (issuer.isPlayer()) {
			sendMessage(issuer.getIssuer(), formatterUtil.formatMessage((Player) issuer.getIssuer(), message, true));
		} else {
			issuer.sendMessage(formatterUtil.cleanMessage(message));
		}
	}

	public void sendMessage(Player sender, String message) {
		if (message == null || message.isEmpty())
			return;

		sendMessage(sender, formatterUtil.formatMessage(sender, message, true));
	}

	public void sendMessage(Player sender, Component message) {
		loggerUtil.trace("Sending actionbar to " + sender.getName());
		platformMessageSender.sendMessage(sender, message);
	}

	public void sendActionBar(Player sender, String message) {
		if (message == null || message.isEmpty())
			return;

		sendActionBar(sender, formatterUtil.formatMessage(sender, message, false));
	}

	public void sendActionBar(Player player, Component message) {
		loggerUtil.trace("Sending action bar to " + player.getName());

		((Audience) player).sendActionBar(message);
	}

	public void sendBossBar(Player player, String message, BossBar.Color color, BossBar.Overlay style, int duration) {
		if (message == null || message.isEmpty())
			return;

		sendBossBar(player, formatterUtil.formatMessage(player, message, false), color, style, duration);
	}

	public void sendBossBar(Player player, Component message, BossBar.Color color, BossBar.Overlay style, int duration) {
		loggerUtil.trace("Sending boss bar to " + player.getName());

		BossBar bossBar = BossBar.bossBar(message, 1.0f, color, style);
		((Audience) player).showBossBar(bossBar);

		long startTime = System.currentTimeMillis();
		scheduler.scheduleAtFixedRate(() -> {
			long elapsedTime = System.currentTimeMillis() - startTime;
			float progress = 1.0f - (float) elapsedTime / (duration * 1000);
			if (progress <= 0) {
				((Audience) player).hideBossBar(bossBar);
			} else {
				bossBar.progress(progress);
			}
		}, 0, 50, TimeUnit.MILLISECONDS, Optional.empty());
	}


	public void sendTitle(Player player, String title, String subtitle, Duration fadeIn, Duration stay, Duration fadeOut) {
		if (title == null || title.isEmpty())
			return;

		sendTitle(player, formatterUtil.formatMessage(player, title, true), formatterUtil.formatMessage(player, subtitle, true), fadeIn, stay, fadeOut);
	}

	public void sendTitle(Player player, Component title, Component subtitle, Duration fadeIn, Duration stay, Duration fadeOut) {
		loggerUtil.trace("Sending title to " + player.getName());

		((Audience) player).showTitle(Title.title(title, subtitle, Title.Times.times(fadeIn, stay, fadeOut)));
	}

	public Component replacePlaceholder(Component component, Object placeholder, Object content) {
		TextReplacementConfig.Builder textReplacementConfigBuilder;
		if (placeholder instanceof Pattern) {
			textReplacementConfigBuilder = TextReplacementConfig.builder().match((Pattern) placeholder);
		} else {
			textReplacementConfigBuilder = TextReplacementConfig.builder().matchLiteral((String) placeholder);
		}

		if (content instanceof String) {
			textReplacementConfigBuilder.replacement((String) content);
		} else {
			textReplacementConfigBuilder.replacement((Component) content);
		}

		TextReplacementConfig textReplacementConfig = textReplacementConfigBuilder.build();

		return component.replaceText(textReplacementConfig);
	}
}
