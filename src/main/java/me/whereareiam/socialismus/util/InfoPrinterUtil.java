package me.whereareiam.socialismus.util;

import com.google.inject.Inject;
import me.whereareiam.socialismus.Socialismus;
import me.whereareiam.socialismus.command.manager.CommandManager;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.integration.IntegrationManager;
import me.whereareiam.socialismus.platform.PlatformType;

import java.util.List;

public class InfoPrinterUtil {
    private static LoggerUtil loggerUtil;
    private static CommandManager commandManager;
    private static IntegrationManager integrationManager;

    @Inject
    public InfoPrinterUtil(LoggerUtil loggerUtil, CommandManager commandManager, IntegrationManager integrationManager) {
        InfoPrinterUtil.loggerUtil = loggerUtil;
        InfoPrinterUtil.commandManager = commandManager;
        InfoPrinterUtil.integrationManager = integrationManager;
    }

    public static void printStartMessage() {
        loggerUtil.info("");
        loggerUtil.info("  █▀ █▀▀   Socialismus v" + Socialismus.version);
        loggerUtil.info("  ▄█ █▄▄   Platform: " + PlatformType.getCurrentPlatform());
        loggerUtil.info("");

        int enabledIntegrationCount = integrationManager.getEnabledIntegrationCount();
        if (enabledIntegrationCount > 0) {
            loggerUtil.info("  Hooked with:");

            List<Integration> enabledIntegrations = integrationManager.getEnabledIntegrations();
            for (Integration integration : enabledIntegrations) {
                loggerUtil.info("    - " + integration.getName());
            }

            loggerUtil.info("");
        }

        int commandCount = commandManager.getCommandCount();
        loggerUtil.info("  Registered " + commandCount + " " + (commandCount == 1 ? "command" : "commands"));
        loggerUtil.info("");
    }
}
