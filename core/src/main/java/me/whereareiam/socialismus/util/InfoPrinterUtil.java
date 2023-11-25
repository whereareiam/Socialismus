package me.whereareiam.socialismus.util;

import com.google.inject.Inject;
import me.whereareiam.socialismus.SocialismusBase;
import me.whereareiam.socialismus.command.management.CommandManager;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.integration.IntegrationManager;
import me.whereareiam.socialismus.integration.placeholderapi.PlaceholderAPI;
import me.whereareiam.socialismus.module.ModuleLoader;
import me.whereareiam.socialismus.platform.PlatformType;

import java.util.List;

public class InfoPrinterUtil {
    private final LoggerUtil loggerUtil;
    private final CommandManager commandManager;
    private final IntegrationManager integrationManager;
    private final ModuleLoader moduleLoader;

    @Inject
    public InfoPrinterUtil(LoggerUtil loggerUtil, CommandManager commandManager, IntegrationManager integrationManager,
                           ModuleLoader moduleLoader) {
        this.loggerUtil = loggerUtil;
        this.commandManager = commandManager;
        this.integrationManager = integrationManager;
        this.moduleLoader = moduleLoader;
    }

    public void printStartMessage() {
        loggerUtil.info("");
        loggerUtil.info("  █▀ █▀▀   Socialismus v" + SocialismusBase.version);
        loggerUtil.info("  ▄█ █▄▄   Platform: " + PlatformType.getCurrentPlatform());
        loggerUtil.info("");

        int enabledIntegrationCount = integrationManager.getEnabledIntegrationCount();
        if (enabledIntegrationCount > 0) {
            loggerUtil.info("  Hooked with:");

            List<Integration> enabledIntegrations = integrationManager.getIntegrations();
            for (Integration integration : enabledIntegrations) {
                loggerUtil.info("    - " + integration.getName());
            }

            loggerUtil.info("");
        }

        int commandCount = commandManager.getCommandCount();
        loggerUtil.info("  Registered " + commandCount + " " + (commandCount == 1 ? "command" : "commands"));

        integrationManager.getIntegrations().forEach(i -> {
            if (i.getName().equals("PlaceholderAPI")) {
                int placeholdersCount = PlaceholderAPI.getPlaceholdersCount();
                loggerUtil.info("  Registered " + placeholdersCount + " " + (placeholdersCount == 1 ? "placeholder" : "placeholders"));
            }
        });

        int chatCount = moduleLoader.getChatCount();
        loggerUtil.info("  Registered " + chatCount + " " + (chatCount == 1 ? "chat" : "chat"));

        int swapperCount = moduleLoader.getSwapperCount();
        loggerUtil.info("  Registered " + swapperCount + " " + (swapperCount == 1 ? "swapper" : "swappers"));

        loggerUtil.info("");
    }
}
