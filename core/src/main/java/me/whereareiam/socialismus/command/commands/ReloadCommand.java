package me.whereareiam.socialismus.command.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.google.inject.Inject;
import me.whereareiam.socialismus.command.base.CommandBase;
import me.whereareiam.socialismus.config.ConfigManager;
import me.whereareiam.socialismus.config.command.CommandsConfig;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.module.ModuleLoader;
import me.whereareiam.socialismus.util.LoggerUtil;
import me.whereareiam.socialismus.util.MessageUtil;
import org.bukkit.entity.Player;

@CommandAlias("%command.main")
public class ReloadCommand extends CommandBase {
    private final LoggerUtil loggerUtil;
    private final MessageUtil messageUtil;
    private final CommandsConfig commands;
    private final MessagesConfig messages;
    private final ConfigManager configManager;
    private final ModuleLoader moduleLoader;

    @Inject
    public ReloadCommand(LoggerUtil loggerUtil, MessageUtil messageUtil, CommandsConfig commands, MessagesConfig messages,
                         ConfigManager configManager, ModuleLoader moduleLoader) {
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
        String message = messages.commands.reloadCommand.reloadedSuccessfully;
        if (issuer.getIssuer() instanceof Player) {
            messageUtil.sendMessage(issuer.getIssuer(), message);
        }

        loggerUtil.info(message);
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
