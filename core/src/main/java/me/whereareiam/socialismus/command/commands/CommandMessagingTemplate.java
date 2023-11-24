package me.whereareiam.socialismus.command.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import me.whereareiam.socialismus.command.base.CommandBase;
import me.whereareiam.socialismus.model.commandmessaging.CommandMessaging;
import me.whereareiam.socialismus.module.Module;
import me.whereareiam.socialismus.requirement.RequirementValidator;
import me.whereareiam.socialismus.util.FormatterUtil;
import me.whereareiam.socialismus.util.LoggerUtil;
import me.whereareiam.socialismus.util.MessageUtil;
import me.whereareiam.socialismus.util.WorldPlayerUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collection;

public class CommandMessagingTemplate extends CommandBase {
    private final LoggerUtil loggerUtil;
    private final MessageUtil messageUtil;
    private final FormatterUtil formatterUtil;

    private final RequirementValidator requirementValidator;

    private CommandMessaging commandMessaging;

    @Inject
    public CommandMessagingTemplate(LoggerUtil loggerUtil, MessageUtil messageUtil, FormatterUtil formatterUtil,
                                    RequirementValidator requirementValidator) {
        this.loggerUtil = loggerUtil;
        this.messageUtil = messageUtil;
        this.formatterUtil = formatterUtil;
        this.requirementValidator = requirementValidator;

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
        if (issuer instanceof Player sender) {
            Collection<? extends Player> recipients = WorldPlayerUtil.getPlayersInWorld(sender.getWorld());
            recipients = requirementValidator.validatePlayers(Module.COMMAND, sender, recipients);

            Component formatComponent = formatterUtil.formatMessage(sender, commandMessaging.format);

            formatComponent = messageUtil.replacePlaceholder(formatComponent, "{playerName}", sender.getName());
            formatComponent = messageUtil.replacePlaceholder(formatComponent, "{message}", message);

            Component finalFormatComponent = formatComponent;
            recipients.forEach(recipient -> messageUtil.sendMessage(recipient, finalFormatComponent));
        } else {
            loggerUtil.info("You can't use this command in console");
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
