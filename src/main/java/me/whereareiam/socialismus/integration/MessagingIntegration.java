package me.whereareiam.socialismus.integration;

import org.bukkit.entity.Player;

public interface MessagingIntegration extends Integration {
    String formatMessage(Player player, String message);
}
