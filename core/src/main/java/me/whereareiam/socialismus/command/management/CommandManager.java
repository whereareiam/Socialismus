package me.whereareiam.socialismus.command.management;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.RootCommand;
import co.aikar.locales.MessageKey;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.command.base.CommandBase;
import me.whereareiam.socialismus.config.message.MessagesConfig;

import java.util.Collection;
import java.util.Locale;

@Singleton
public class CommandManager {
    private final Injector injector;
    private final BukkitCommandManager bukkitCommandManager;
    private final MessagesConfig messagesConfig;

    private int commandCount = 0;

    @Inject
    public CommandManager(Injector injector, BukkitCommandManager bukkitCommandManager, MessagesConfig messagesConfig) {
        this.injector = injector;
        this.bukkitCommandManager = bukkitCommandManager;
        this.messagesConfig = messagesConfig;

        addTranslations();
    }

    public void registerCommand(CommandBase command) {
        if (command.isEnabled()) {
            command.addReplacements();
            bukkitCommandManager.registerCommand(command);
        }
        commandCount++;
    }

    public void addTranslations() {
        addTranslation(messagesConfig.commands.unknownCommand, "acf-core.permission_denied");
        addTranslation(messagesConfig.commands.unknownCommand, "acf-core.permission_denied_parameter");
        addTranslation(messagesConfig.commands.wrongSyntax, "acf-core.invalid_syntax");
        addTranslation(messagesConfig.commands.unknownCommand, "acf-core.unknown_command");
        addTranslation(messagesConfig.commands.errorOccurred, "acf-core.error_performing_command");
        addTranslation(messagesConfig.commands.missingArgument, "acf-core.please_specify_one_of");
    }

    private void addTranslation(String message, String acfKey) {
        bukkitCommandManager.getLocales().addMessage(Locale.ENGLISH, MessageKey.of(acfKey), message);
    }

    public int getCommandCount() {
        return commandCount;
    }

    public Collection<RootCommand> getAllCommands() {
        return bukkitCommandManager.getRegisteredRootCommands();
    }
}
