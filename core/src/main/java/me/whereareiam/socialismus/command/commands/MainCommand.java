package me.whereareiam.socialismus.command.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.RootCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
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
import net.kyori.adventure.text.Component;
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

    @CommandAlias("%command.main")
    @CommandPermission("%permission.main")
    @Description("%description.main")
    public void onCommand(CommandIssuer issuer) {
        if (issuer.getIssuer() instanceof Player) {
            System.out.println("Player");
            Player player = issuer.getIssuer();
            Component message = formatterUtil.formatMessage(player, buildHelpCommand(issuer));
            messageUtil.sendMessage(player, message);
        } else {
            issuer.sendMessage(formatterUtil.cleanMessage(buildHelpCommand(issuer)));
        }
    }

    @Cacheable(duration = 1)
    private String buildHelpCommand(CommandIssuer issuer) {
        Map<RootCommand, Set<RegisteredCommand>> commands = getAllowedCommands(getCommands(), issuer);

        StringBuilder formattedCommands = new StringBuilder();
        for (Map.Entry<RootCommand, Set<RegisteredCommand>> entry : commands.entrySet()) {
            RootCommand rootCommand = entry.getKey();
            Set<RegisteredCommand> registeredCommands = entry.getValue();

            if (registeredCommands.isEmpty()) {
                String commandFormat = messages.commands.mainCommand.commandFormat
                        .replace("{command}", rootCommand.getCommandName())
                        .replace("{subCommand}", "")
                        .replace("{description}", rootCommand.getDescription());
                formattedCommands.append(commandFormat).append("\n");
            } else {
                for (RegisteredCommand registeredCommand : registeredCommands) {
                    String commandFormat = messages.commands.mainCommand.commandFormat
                            .replace("{command}", rootCommand.getCommandName())
                            .replace("{subCommand}", " " + registeredCommand.getPrefSubCommand())
                            .replace("{description}", registeredCommand.getHelpText());
                    formattedCommands.append(commandFormat).append("\n");
                }
            }
        }

        List<String> helpFormat = messages.commands.mainCommand.helpFormat;
        for (int i = 0; i < helpFormat.size(); i++) {
            if (helpFormat.get(i).contains("{commands}")) {
                String formattedCommandsStr = formattedCommands.toString();
                if (formattedCommandsStr.endsWith("\n")) {
                    formattedCommandsStr = formattedCommandsStr.substring(0, formattedCommandsStr.length() - 1);
                }

                helpFormat.set(i, helpFormat.get(i).replace("{commands}", formattedCommandsStr));
            }
        }

        return String.join("\n", helpFormat);
    }

    @Cacheable(duration = 20)
    private Map<RootCommand, Set<RegisteredCommand>> getCommands() {
        Map<RootCommand, Set<RegisteredCommand>> commands = new HashMap<>();

        Collection<RootCommand> allCommands = commandManager.getAllCommands();
        Set<String> uniqueParentClassNames = new HashSet<>();

        for (RootCommand rootCommand : allCommands) {
            String parentClassName = rootCommand.getDefCommand().getName();

            if (!parentClassName.equals("chatcommandtemplate") && !uniqueParentClassNames.contains(parentClassName)) {
                uniqueParentClassNames.add(parentClassName);

                HashSet<RegisteredCommand> uniqueSubCommands = new HashSet<>();
                rootCommand.getSubCommands().entries().forEach(entry -> {
                    RegisteredCommand registeredCommand = entry.getValue();

                    if (!uniqueSubCommands.contains(registeredCommand) && !registeredCommand.equals(rootCommand.getDefaultRegisteredCommand())) {
                        uniqueSubCommands.add(registeredCommand);
                    }
                });

                commands.put(rootCommand, uniqueSubCommands);
            }
        }

        return commands;
    }


    public Map<RootCommand, Set<RegisteredCommand>> getAllowedCommands(Map<RootCommand, Set<RegisteredCommand>> commands,
                                                                       CommandIssuer issuer) {
        Map<RootCommand, Set<RegisteredCommand>> filteredCommands = new HashMap<>();

        for (Map.Entry<RootCommand, Set<RegisteredCommand>> entry : commands.entrySet()) {
            RootCommand rootCommand = entry.getKey();
            Set<RegisteredCommand> registeredCommands = entry.getValue();

            if (rootCommand.getUniquePermission() == null || issuer.hasPermission(rootCommand.getUniquePermission())) {
                Set<RegisteredCommand> filteredRegisteredCommands = new HashSet<>();

                for (RegisteredCommand registeredCommand : registeredCommands) {
                    Set<String> requiredPermissions = registeredCommand.getRequiredPermissions();

                    boolean hasAllPermissions = requiredPermissions.stream().allMatch(issuer::hasPermission);

                    if (hasAllPermissions) {
                        filteredRegisteredCommands.add(registeredCommand);
                    }
                }

                filteredCommands.put(rootCommand, filteredRegisteredCommands);
            }
        }

        return filteredCommands;
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
