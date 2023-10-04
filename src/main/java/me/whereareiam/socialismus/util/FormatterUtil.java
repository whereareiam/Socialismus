package me.whereareiam.socialismus.util;

import com.google.inject.Inject;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.integration.IntegrationManager;
import me.whereareiam.socialismus.integration.IntegrationType;
import me.whereareiam.socialismus.integration.MessagingIntegration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class FormatterUtil {
    private static IntegrationManager integrationManager;

    @Inject
    public FormatterUtil(IntegrationManager integrationManager) {
        FormatterUtil.integrationManager = integrationManager;
    }

    public static Component formatMessage(String message) {
        return formatMessage(null, message);
    }

    public static Component formatMessage(Player player, String message) {
        final MiniMessage miniMessage = MiniMessage.miniMessage();

        message = hookIntegration(player, message);

        return miniMessage.deserialize(message);
    }

    public static String hookIntegration(Player player, String message) {
        for (Integration integration : integrationManager.getEnabledIntegrations()) {
            if (integration.getType() == IntegrationType.MESSAGING) {
                MessagingIntegration formatterIntegration = (MessagingIntegration) integration;
                message = formatterIntegration.formatMessage(player, message);
            }
        }

        return message;
    }

}
