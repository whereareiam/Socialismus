package me.whereareiam.socialismus.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.google.inject.Inject;
import me.whereareiam.socialismus.config.MessagesConfig;

@CommandAlias("%command.main")
public class ReloadCommand extends BaseCommand {
    private final MessagesConfig messagesConfig;

    @Inject
    public ReloadCommand(MessagesConfig messagesConfig) {
        this.messagesConfig = messagesConfig;
    }

    @Subcommand("%command.reload")
    @CommandPermission("%permission.reload")
    public void onCommand(CommandIssuer issuer) {
        System.out.println(messagesConfig.commands.unknownCommand);
    }
}
