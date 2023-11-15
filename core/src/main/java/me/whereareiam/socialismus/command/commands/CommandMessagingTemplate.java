package me.whereareiam.socialismus.command.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import me.whereareiam.socialismus.command.base.CommandBase;
import me.whereareiam.socialismus.model.commandmessaging.CommandMessaging;
import me.whereareiam.socialismus.util.*;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collection;

public class CommandMessagingTemplate extends CommandBase {
    private final LoggerUtil loggerUtil;
    private final MessageUtil messageUtil;
    private final FormatterUtil formatterUtil;

    private CommandMessaging commandMessaging;

    @Inject
    public CommandMessagingTemplate(LoggerUtil loggerUtil, MessageUtil messageUtil, FormatterUtil formatterUtil) {
        this.loggerUtil = loggerUtil;
        this.messageUtil = messageUtil;
        this.formatterUtil = formatterUtil;

        loggerUtil.trace("Initializing class: " + this);
    }

    public void setRolePlay(me.whereareiam.socialismus.model.commandmessaging.CommandMessaging commandMessaging) {
        this.commandMessaging = commandMessaging;
    }

    @CommandAlias("%command.commandMessaging")
    @CommandPermission("%permission.commandMessaging")
    @CommandCompletion("@nothing")
    @Description("description.commandMessaging")
    @Syntax("%syntax.commandMessaging")
    public void onCommand(CommandIssuer issuer, String message) {
        if (issuer instanceof Player player) {
            Collection<Player> onlinePlayers = WorldPlayerUtil.getPlayersInWorld(player.getWorld());
            Component formatComponent = formatterUtil.formatMessage(player, commandMessaging.format);

            formatComponent = messageUtil.replacePlaceholder(formatComponent, "{playerName}", player.getName());
            formatComponent = messageUtil.replacePlaceholder(formatComponent, "{message}", message);

            Component finalFormatComponent = formatComponent;
            onlinePlayers.stream()
                    .filter(recipient -> commandMessaging.requirements.radius == -1 || DistanceCalculatorUtil.calculateDistance(player, recipient) <= commandMessaging.requirements.radius)
                    .forEach(recipient -> messageUtil.sendMessage(recipient, finalFormatComponent));
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