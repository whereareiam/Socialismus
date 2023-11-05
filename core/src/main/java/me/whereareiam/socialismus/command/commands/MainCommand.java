package me.whereareiam.socialismus.command.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.RootCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import com.google.common.collect.SetMultimap;
import com.google.inject.Inject;
import me.whereareiam.socialismus.cache.Cacheable;
import me.whereareiam.socialismus.command.base.CommandBase;
import me.whereareiam.socialismus.command.management.CommandManager;
import me.whereareiam.socialismus.config.command.CommandsConfig;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.util.FormatterUtil;
import me.whereareiam.socialismus.util.LoggerUtil;
import me.whereareiam.socialismus.util.MessageUtil;
import org.bukkit.entity.Player;

import java.util.*;

public class MainCommand extends CommandBase {
    private final LoggerUtil loggerUtil;
    private final CommandManager commandManager;
    private final MessageUtil messageUtil;
    private final FormatterUtil formatterUtil;
    private final CommandsConfig commands;
    private final MessagesConfig messages;

    @Inject
    public MainCommand(LoggerUtil loggerUtil, CommandManager commandManager, MessageUtil messageUtil,
                       FormatterUtil formatterUtil, CommandsConfig commands, MessagesConfig messages) {
        this.loggerUtil = loggerUtil;
        this.commandManager = commandManager;
        this.messageUtil = messageUtil;
        this.formatterUtil = formatterUtil;
        this.commands = commands;
        this.messages = messages;
    }

    @CommandAlias("%command.main")
    @CommandPermission("%permission.main")
    @Description("%description.main")
    public void onCommand(CommandIssuer issuer) {
        List<RootCommand> allCommands = new ArrayList<>(commandManager.getAllCommands());
        if (allCommands.isEmpty()) {
            return;
        }

        RootCommand command = allCommands.get(0);
        String commandsBuilder = buildCommands(issuer, command);

        String helpMessage = formatHelpMessage(commandsBuilder);
        sendHelpMessage(issuer, helpMessage);
    }

    @Cacheable
    private String buildCommands(CommandIssuer issuer, RootCommand command) {
        StringJoiner commandsJoiner = new StringJoiner("\n");

        String mainCommandHelp = formatMainCommandHelp(command);
        commandsJoiner.add(mainCommandHelp);

        Map<String, List<String>> descriptionToSubcommands = getDescriptionToSubcommands(issuer, command);

        for (Map.Entry<String, List<String>> descEntry : descriptionToSubcommands.entrySet()) {
            if (!descEntry.getValue().isEmpty()) {
                String commandHelp = formatSubCommandHelp(command, descEntry);
                commandsJoiner.add(commandHelp);
            }
        }

        return commandsJoiner.toString();
    }

    @Cacheable
    private String formatMainCommandHelp(RootCommand command) {
        return messages.commands.mainCommand.commandHelpFormat
                .replace("{command}", command.getCommandName())
                .replace("{subCommand}", "")
                .replace("{description}", command.getDescription());
    }

    @Cacheable
    private Map<String, List<String>> getDescriptionToSubcommands(CommandIssuer issuer, RootCommand command) {
        Map<String, List<String>> descriptionToSubcommands = new HashMap<>();
        SetMultimap<String, RegisteredCommand> subCommands = command.getSubCommands();

        for (Map.Entry<String, Collection<RegisteredCommand>> subEntry : subCommands.asMap().entrySet()) {
            for (RegisteredCommand<?> subCommand : subEntry.getValue()) {
                String description = subCommand.getHelpText();
                if (!descriptionToSubcommands.containsKey(description)) {
                    descriptionToSubcommands.put(description, new ArrayList<>());
                }
                if (!subEntry.getKey().equals("__default")) {
                    if (issuer.getIssuer() instanceof Player && !hasRequiredPermissions(issuer.getIssuer(), subCommand)) {
                        continue;
                    }
                    descriptionToSubcommands.get(description).add(subEntry.getKey());
                }
            }
        }

        return descriptionToSubcommands;
    }

    private boolean hasRequiredPermissions(Player player, RegisteredCommand<?> subCommand) {
        for (String permission : subCommand.getRequiredPermissions()) {
            loggerUtil.trace("Checking permission " + permission + " for " + player.getName());
            if (player.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    @Cacheable
    private String formatSubCommandHelp(RootCommand command, Map.Entry<String, List<String>> descEntry) {
        return messages.commands.mainCommand.commandHelpFormat
                .replace("{command}", command.getCommandName())
                .replace("{subCommand}", " " + descEntry.getValue().get(0))
                .replace("{description}", descEntry.getKey());
    }

    @Cacheable
    private String formatHelpMessage(String commands) {
        return String.join("\n", messages.commands.mainCommand.helpFormat)
                .replace("{commands}", commands);
    }

    private void sendHelpMessage(CommandIssuer issuer, String helpMessage) {
        if (issuer.getIssuer() instanceof Player) {
            messageUtil.sendMessage(issuer.getIssuer(), helpMessage);
        } else {
            issuer.sendMessage(formatterUtil.cleanMessage(helpMessage));
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void addReplacements() {
        commandHelper.addReplacement(commands.mainCommand.command, "command.main");
        commandHelper.addReplacement(commands.mainCommand.permission, "permission.main");
        commandHelper.addReplacement(messages.commands.mainCommand.description, "description.main");
    }
}
