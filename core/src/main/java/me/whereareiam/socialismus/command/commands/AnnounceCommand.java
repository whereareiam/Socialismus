package me.whereareiam.socialismus.command.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.command.base.CommandBase;
import me.whereareiam.socialismus.config.command.CommandsConfig;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.util.LoggerUtil;
import me.whereareiam.socialismus.util.MessageUtil;

@Singleton
@CommandAlias("%command.main")
public class AnnounceCommand extends CommandBase {
    private final LoggerUtil loggerUtil;
    private final MessageUtil messageUtil;
    private final CommandsConfig commands;
    private final MessagesConfig messages;

    @Inject
    public AnnounceCommand(LoggerUtil loggerUtil, MessageUtil messageUtil, CommandsConfig commands,
                           MessagesConfig messages) {
        this.loggerUtil = loggerUtil;
        this.messageUtil = messageUtil;
        this.commands = commands;
        this.messages = messages;
    }

    @Subcommand("%command.announce")
    @CommandPermission("%permission.announce")
    @Description("%description.announce")
    public void onCommand(CommandIssuer issuer) {

    }

    @Override
    public boolean isEnabled() {
        return commands.announceCommand.enabled;
    }

    @Override
    public void addReplacements() {
        commandHelper.addReplacement(commands.announceCommand.subCommand, "command.announce");
        commandHelper.addReplacement(commands.announceCommand.permission, "permission.announce");
        commandHelper.addReplacement(messages.commands.announceCommand.description, "description.announce");
    }
}
