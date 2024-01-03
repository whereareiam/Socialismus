package me.whereareiam.socialismus.command.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.RootCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import com.google.common.collect.SetMultimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
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

@Singleton
public class MainCommand extends CommandBase {
    private final LoggerUtil loggerUtil;
    private final CommandManager commandManager;
    private final FormatterUtil formatterUtil;
    private final MessageUtil messageUtil;
    private final CommandsConfig commands;
    private final MessagesConfig messages;

    @Inject
    public MainCommand(LoggerUtil loggerUtil, CommandManager commandManager,
                       FormatterUtil formatterUtil, MessageUtil messageUtil, CommandsConfig commands, MessagesConfig messages) {
        this.loggerUtil = loggerUtil;
        this.commandManager = commandManager;
        this.formatterUtil = formatterUtil;
        this.messageUtil = messageUtil;
        this.commands = commands;
        this.messages = messages;
    }

    @Cacheable(duration = 1)
    @CommandAlias("%command.main")
    @CommandPermission("%permission.main")
    @Description("%description.main")
    public void onCommand(CommandIssuer issuer) {
        List<RootCommand> allCommands = new ArrayList<>(commandManager.getAllCommands());
        if (allCommands.isEmpty()) {
            return;
        }

        String mainCommand = commands.mainCommand.command.split("\\|")[0];
        String privateMessageCommand = commands.privateMessageCommand.command.split("\\|")[0];

        List<String> commandsList = new ArrayList<>();
        for (RootCommand command : allCommands) {
            if (command.getCommandName().equals(mainCommand) || command.getCommandName().equals(privateMessageCommand)) {
                String commandHelp = buildCommands(issuer, command);
                commandsList.add(commandHelp);
            }
        }

        String commandsString = String.join("\n", commandsList);
        String helpMessage = formatHelpMessage(commandsString);
        sendHelpMessage(issuer, helpMessage);
    }

    private String buildCommands(CommandIssuer issuer, RootCommand command) {
        List<String> commands = new ArrayList<>();

        String mainCommandHelp = formatMainCommandHelp(command);
        commands.add(mainCommandHelp);

        Map<String, List<String>> descriptionToSubcommands = getDescriptionToSubcommands(issuer, command);

        for (Map.Entry<String, List<String>> descEntry : descriptionToSubcommands.entrySet()) {
            if (!descEntry.getValue().isEmpty()) {
                String commandHelp = formatSubCommandHelp(command, descEntry);
                commands.add(commandHelp);
            }
        }

        return String.join("\n", commands);
    }

    private String formatMainCommandHelp(RootCommand command) {
        return messages.commands.mainCommand.commandHelpFormat
                .replace("{command}", command.getCommandName())
                .replace("{subCommand}", "")
                .replace("{description}", command.getDescription());
    }

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

    private String formatSubCommandHelp(RootCommand command, Map.Entry<String, List<String>> descEntry) {
        List<String> subcommands = new ArrayList<>();

        for (String subcommand : descEntry.getValue()) {
            String formattedSubcommand = messages.commands.mainCommand.commandHelpFormat
                    .replace("{command}", command.getCommandName())
                    .replace("{subCommand}", " " + subcommand)
                    .replace("{description}", descEntry.getKey());
            subcommands.add(formattedSubcommand);
        }

        return String.join("\n", subcommands);
    }

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
