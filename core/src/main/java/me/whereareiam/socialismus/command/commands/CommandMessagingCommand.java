package me.whereareiam.socialismus.command.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import me.whereareiam.socialismus.command.base.CommandBase;
import me.whereareiam.socialismus.model.commandmessaging.CommandMessaging;
import me.whereareiam.socialismus.util.FormatterUtil;
import me.whereareiam.socialismus.util.LoggerUtil;
import me.whereareiam.socialismus.util.MessageUtil;
import me.whereareiam.socialismus.util.WorldPlayerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.entity.Player;

import java.util.Collection;

public class CommandMessagingCommand extends CommandBase {
    private final LoggerUtil loggerUtil;
    private final FormatterUtil formatterUtil;

    private CommandMessaging commandMessaging;

    @Inject
    public CommandMessagingCommand(LoggerUtil loggerUtil, FormatterUtil formatterUtil) {
        this.loggerUtil = loggerUtil;
        this.formatterUtil = formatterUtil;

        loggerUtil.trace("Initializing class: " + this);
    }

    public void setRolePlay(CommandMessaging commandMessaging) {
        this.commandMessaging = commandMessaging;
    }

    @CommandAlias("%command.commandMessaging")
    @CommandPermission("%permission.commandMessaging")
    @CommandCompletion("@nothing")
    @Description("description.commandMessaging")
    @Syntax("%syntax.commandMessaging")
    public void onCommand(CommandIssuer issuer, String message) {
        if (issuer instanceof Player player) {
            Collection<Player> players = WorldPlayerUtil.getPlayersInWorld(player.getWorld());
            Component formatComponent = formatterUtil.formatMessage(player, commandMessaging.format);

            TextReplacementConfig internalPlaceholders = TextReplacementConfig.builder()
                    .matchLiteral("{playerName}")
                    .replacement(player.getName())
                    .matchLiteral("{message}")
                    .replacement(message)
                    .build();

            formatComponent = formatComponent.replaceText(internalPlaceholders);

            for (Player recipient : players) {
                MessageUtil.sendMessage(recipient, formatComponent);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return commandMessaging.enabled;
    }

    @Override
    public void addReplacements() {
        commandHelper.addReplacement(commandMessaging.command, "command.commandMessaging");
        commandHelper.addReplacement(commandMessaging.requirements.writePermission, "permission.commandMessaging");
        commandHelper.addReplacement(commandMessaging.description, "description.commandMessaging");
        commandHelper.addReplacement(commandMessaging.syntax, "syntax.commandMessaging");
    }
}
