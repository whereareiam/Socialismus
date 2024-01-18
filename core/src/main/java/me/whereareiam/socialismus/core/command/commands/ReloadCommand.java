package me.whereareiam.socialismus.core.command.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.core.command.base.CommandBase;
import me.whereareiam.socialismus.core.config.ConfigManager;
import me.whereareiam.socialismus.core.config.command.CommandsConfig;
import me.whereareiam.socialismus.core.config.message.MessagesConfig;
import me.whereareiam.socialismus.core.module.ModuleLoader;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import me.whereareiam.socialismus.core.util.MessageUtil;

@Singleton
@CommandAlias("%command.main")
public class ReloadCommand extends CommandBase {
	private final LoggerUtil loggerUtil;
	private final MessageUtil messageUtil;
	private final CommandsConfig commands;
	private final MessagesConfig messages;
	private final ConfigManager configManager;
	private final ModuleLoader moduleLoader;

	@Inject
	public ReloadCommand(LoggerUtil loggerUtil, MessageUtil messageUtil, CommandsConfig commands,
						 MessagesConfig messages, ConfigManager configManager, ModuleLoader moduleLoader) {
		this.loggerUtil = loggerUtil;
		this.messageUtil = messageUtil;
		this.commands = commands;
		this.messages = messages;
		this.configManager = configManager;
		this.moduleLoader = moduleLoader;
	}

	@Subcommand("%command.reload")
	@CommandPermission("%permission.reload")
	@Description("%description.reload")
	public void onCommand(CommandIssuer issuer) {
		messageUtil.sendMessage(issuer, messages.commands.reloadCommand.reloadedSuccessfully);

		configManager.reloadConfigs();
		moduleLoader.reloadModules();
	}

	@Override
	public boolean isEnabled() {
		return commands.reloadCommand.enabled;
	}

	@Override
	public void addReplacements() {
		commandHelper.addReplacement(commands.reloadCommand.subCommand, "command.reload");
		commandHelper.addReplacement(commands.reloadCommand.permission, "permission.reload");
		commandHelper.addReplacement(messages.commands.reloadCommand.description, "description.reload");
	}
}
