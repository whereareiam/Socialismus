package me.whereareiam.socialismus.util;

import com.google.inject.Inject;
import me.whereareiam.socialismus.Socialismus;
import me.whereareiam.socialismus.command.manager.CommandManager;
import me.whereareiam.socialismus.util.platform.PlatformType;

public class InfoPrinter {
    private static Logger logger;
    private static CommandManager commandManager;

    @Inject
    public InfoPrinter(Logger logger, CommandManager commandManager) {
        InfoPrinter.logger = logger;
        InfoPrinter.commandManager = commandManager;
    }

    public static void printStartMessage() {
        logger.info("");
        logger.info("  █▀ █▀▀   Socialismus v" + Socialismus.version);
        logger.info("  ▄█ █▄▄   Platform: " + PlatformType.getCurrentPlatform());
        logger.info("");

        int commandCount = commandManager.getCommandCount();
        logger.info("  Registered " + commandCount + " " + (commandCount == 1 ? "command" : "commands"));
        logger.info("");
    }
}
