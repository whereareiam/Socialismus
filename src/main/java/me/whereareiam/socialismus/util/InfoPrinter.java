package me.whereareiam.socialismus.util;

import com.google.inject.Inject;
import me.whereareiam.socialismus.Socialismus;
import me.whereareiam.socialismus.command.manager.CommandManager;
import me.whereareiam.socialismus.util.integration.Integration;
import me.whereareiam.socialismus.util.integration.IntegrationManager;
import me.whereareiam.socialismus.util.platform.PlatformType;

import java.util.List;

public class InfoPrinter {
    private static Logger logger;
    private static CommandManager commandManager;
    private static IntegrationManager integrationManager;

    @Inject
    public InfoPrinter(Logger logger, CommandManager commandManager, IntegrationManager integrationManager) {
        InfoPrinter.logger = logger;
        InfoPrinter.commandManager = commandManager;
        InfoPrinter.integrationManager = integrationManager;
    }

    public static void printStartMessage() {
        logger.info("");
        logger.info("  █▀ █▀▀   Socialismus v" + Socialismus.version);
        logger.info("  ▄█ █▄▄   Platform: " + PlatformType.getCurrentPlatform());
        logger.info("");

        int enabledIntegrationCount = integrationManager.getEnabledIntegrationCount();
        if (enabledIntegrationCount > 0) {
            logger.info("  Hooked with:");

            List<Integration> enabledIntegrations = integrationManager.getEnabledIntegrations();
            for (Integration integration : enabledIntegrations) {
                logger.info("    - " + integration.getName());
            }

            logger.info("");
        }

        int commandCount = commandManager.getCommandCount();
        logger.info("  Registered " + commandCount + " " + (commandCount == 1 ? "command" : "commands"));
        logger.info("");
    }
}
