package me.whereareiam.socialismus.command.manager;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.locales.MessageKey;
import com.google.inject.Inject;
import com.google.inject.Injector;
import me.whereareiam.socialismus.config.CommandsConfig;
import me.whereareiam.socialismus.config.MessagesConfig;
import org.bukkit.plugin.Plugin;

import java.util.Locale;

public class CommandManager {
    private final Injector injector;
    private final MessagesConfig messagesConfig;
    private final CommandsConfig commandsConfig;
    private final BukkitCommandManager bukkitCommandManager;
    private int commandCount = 0;

    @Inject
    public CommandManager(Injector injector, Plugin plugin, MessagesConfig messagesConfig, CommandsConfig commandsConfig) {
        this.injector = injector;
        this.messagesConfig = messagesConfig;
        this.commandsConfig = commandsConfig;
        this.bukkitCommandManager = new BukkitCommandManager(plugin);

        addReplacements();
        addTranslations();
    }

    public void registerCommand(Class<? extends BaseCommand> commandClass) {
        try {
            BaseCommand commandInstance = injector.getInstance(commandClass);
            bukkitCommandManager.registerCommand(commandInstance);
            commandCount++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addTranslations() {
        addTranslation(messagesConfig.commands.unknownCommand, "acf-core.permission_denied");
        addTranslation(messagesConfig.commands.unknownCommand, "acf-core.permission_denied_parameter");
        addTranslation(messagesConfig.commands.wrongSyntax, "acf-core.invalid_syntax");
        addTranslation(messagesConfig.commands.unknownCommand, "acf-core.unknown_command");
        addTranslation(messagesConfig.commands.errorOccurred, "acf-core.error_performing_command");
        addTranslation(messagesConfig.commands.missingArgument, "acf-core.please_specify_one_of");
    }

    public void addReplacements() {
        addReplacement(commandsConfig.mainCommand, "command.main");
        addReplacement(commandsConfig.reloadCommand.subCommand, "command.reload");
        addReplacement(commandsConfig.reloadCommand.permission, "permission.reload");
    }

    private void addReplacement(String message, String replacementId) {
        bukkitCommandManager.getCommandReplacements().addReplacement(replacementId, message);
    }

    private void addTranslation(String message, String acfKey) {
        bukkitCommandManager.getLocales().addMessage(Locale.ENGLISH, MessageKey.of(acfKey), message);
    }

    public int getCommandCount() {
        return commandCount;
    }
}

