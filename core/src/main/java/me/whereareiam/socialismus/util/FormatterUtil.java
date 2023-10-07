package me.whereareiam.socialismus.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.integration.IntegrationManager;
import me.whereareiam.socialismus.integration.IntegrationType;
import me.whereareiam.socialismus.integration.MessagingIntegration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

@Singleton
public class FormatterUtil {
    private final LoggerUtil loggerUtil;
    private final IntegrationManager integrationManager;

    @Inject
    public FormatterUtil(LoggerUtil loggerUtil, IntegrationManager integrationManager) {
        this.loggerUtil = loggerUtil;
        this.integrationManager = integrationManager;

        loggerUtil.trace("Initializing class: " + this);
    }

    public Component formatMessage(String message) {
        loggerUtil.debug("formatMessage: " + message);

        return formatMessage(null, message);
    }

    public Component formatMessage(Player player, String message) {
        loggerUtil.debug("formatMessage: " + player.getName() + " " + message);
        final MiniMessage miniMessage = MiniMessage.miniMessage();

        message = hookIntegration(player, message);

        return miniMessage.deserialize(message);
    }

    public String hookIntegration(Player player, String message) {
        for (Integration integration : integrationManager.getEnabledIntegrations()) {
            if (integration.getType() == IntegrationType.MESSAGING) {
                MessagingIntegration formatterIntegration = (MessagingIntegration) integration;
                message = formatterIntegration.formatMessage(player, message);

                loggerUtil.trace("Hooked with MESSAGING integration: " + formatterIntegration.getName());
            }
        }

        return message;
    }
}
