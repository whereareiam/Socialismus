package me.whereareiam.socialismus.core.integration;

import org.bukkit.entity.Player;

public interface MessagingIntegration extends Integration {
	String formatMessage(Player player, String string);
}
