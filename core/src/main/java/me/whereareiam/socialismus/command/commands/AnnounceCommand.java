package me.whereareiam.socialismus.command.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
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
    @CommandCompletion("@announcements")
    @CommandPermission("%permission.announce")
    @Description("%description.announce")
    @Syntax("%syntax.announce")
    public void onCommand(CommandIssuer issuer, String announcementId) {

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
        commandHelper.addReplacement(commands.announceCommand.syntax, "syntax.announce");
    }
}
