package me.whereareiam.socialismus.core.util;

import com.google.inject.Inject;
import me.whereareiam.socialismus.core.AbstractSocialismus;
import me.whereareiam.socialismus.core.SocialismusModuleLoader;
import me.whereareiam.socialismus.core.command.management.CommandManager;
import me.whereareiam.socialismus.core.integration.Integration;
import me.whereareiam.socialismus.core.integration.IntegrationManager;
import me.whereareiam.socialismus.core.platform.PlatformType;

import java.util.List;

public class InfoPrinterUtil {
	private final LoggerUtil loggerUtil;
	private final CommandManager commandManager;
	private final IntegrationManager integrationManager;
	private final SocialismusModuleLoader moduleLoader;

	@Inject
	public InfoPrinterUtil(LoggerUtil loggerUtil, CommandManager commandManager, IntegrationManager integrationManager,
	                       SocialismusModuleLoader moduleLoader) {
		this.loggerUtil = loggerUtil;
		this.commandManager = commandManager;
		this.integrationManager = integrationManager;
		this.moduleLoader = moduleLoader;
	}

	public void printStartMessage() {
		loggerUtil.info("");
		loggerUtil.info("  █▀ █▀▀   Socialismus v" + AbstractSocialismus.version);
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

		/*integrationManager.getIntegrations().forEach(i -> {
			if (i.getName().equals("PlaceholderAPI")) {
				int placeholdersCount = PlaceholderAPI.getPlaceholdersCount();
				loggerUtil.info("  Registered " + placeholdersCount + " " + (placeholdersCount == 1 ? "placeholder" : "placeholders"));
			}
		});*/

		int chatCount = moduleLoader.getChatCount();
		loggerUtil.info("  Registered " + chatCount + " " + (chatCount == 1 ? "chat" : "chats"));

		int swapperCount = moduleLoader.getSwapperCount();
		loggerUtil.info("  Registered " + swapperCount + " " + (swapperCount == 1 ? "swapper" : "swappers"));

		int announcementCount = moduleLoader.getAnnouncementsCount();
		loggerUtil.info("  Registered " + announcementCount + " " + (announcementCount == 1 ? "announcement" : "announcements"));

		loggerUtil.info("");
	}
}
