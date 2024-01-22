package me.whereareiam.socialismus.core.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.core.integration.Integration;
import me.whereareiam.socialismus.core.integration.IntegrationManager;
import me.whereareiam.socialismus.core.integration.IntegrationType;
import me.whereareiam.socialismus.core.integration.MessagingIntegration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class FormatterUtil {
	private final LoggerUtil loggerUtil;
	private final IntegrationManager integrationManager;

	private final Map<String, String> colorMap = new HashMap<>();

	@Inject
	public FormatterUtil(LoggerUtil loggerUtil, IntegrationManager integrationManager) {
		this.loggerUtil = loggerUtil;
		this.integrationManager = integrationManager;

		loggerUtil.trace("Initializing class: " + this);

		colorMap.put("&0", "<black>");
		colorMap.put("&1", "<dark_blue>");
		colorMap.put("&2", "<dark_green>");
		colorMap.put("&3", "<dark_aqua>");
		colorMap.put("&4", "<dark_red>");
		colorMap.put("&5", "<dark_purple>");
		colorMap.put("&6", "<gold>");
		colorMap.put("&7", "<gray>");
		colorMap.put("&8", "<dark_gray>");
		colorMap.put("&9", "<blue>");
		colorMap.put("&a", "<green>");
		colorMap.put("&b", "<aqua>");
		colorMap.put("&c", "<red>");
		colorMap.put("&d", "<light_purple>");
		colorMap.put("&e", "<yellow>");
		colorMap.put("&f", "<white>");
		colorMap.put("&k", "<obfuscated>");
		colorMap.put("&l", "<bold>");
		colorMap.put("&m", "<strikethrough>");
		colorMap.put("&n", "<underline>");
		colorMap.put("&o", "<italic>");
		colorMap.put("&r", "<reset>");
	}

	public String cleanMessage(String message) {
		message = message.replaceAll("<[^>]*>", "");

		message = message.replaceAll("&[0-9a-fk-or]", "");
		message = message.replaceAll("§[0-9a-fk-or]", "");

		return message;
	}

	public Component formatMessage(String message) {
		return formatMessage(null, message);
	}

	public Component formatMessage(Player player, String message) {
		loggerUtil.trace("formatMessage:" + message);
		final MiniMessage miniMessage = MiniMessage.miniMessage();

		if (message == null || message.isEmpty())
			return miniMessage.deserialize("");

		if (player != null)
			message = hookIntegration(player, message);

		message = convertLegacyColorCodes(message);

		return miniMessage.deserialize(message);
	}

	public String hookIntegration(Player player, String message) {
		for (Integration integration : integrationManager.getIntegrations()) {
			if (integration.getType() == IntegrationType.MESSAGING) {
				MessagingIntegration formatterIntegration = (MessagingIntegration) integration;
				message = formatterIntegration.formatMessage(player, message);

				loggerUtil.trace("Hooked with MESSAGING integration: " + formatterIntegration.getName());
			}
		}

		return message;
	}

	private String convertLegacyColorCodes(String message) {
		for (Map.Entry<String, String> entry : colorMap.entrySet()) {
			message = message.replace(entry.getKey(), entry.getValue());
			message = message.replace(entry.getKey().replace("&", "§"), entry.getValue());
		}

		return message;
	}
}
