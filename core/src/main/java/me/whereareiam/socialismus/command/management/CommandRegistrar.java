package me.whereareiam.socialismus.command.management;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.command.commands.CommandMessagingTemplate;
import me.whereareiam.socialismus.command.commands.MainCommand;
import me.whereareiam.socialismus.command.commands.PrivateMessageCommand;
import me.whereareiam.socialismus.command.commands.ReloadCommand;
import me.whereareiam.socialismus.config.command.CommandsConfig;

@Singleton
public class CommandRegistrar {
    private final Injector injector;
    private final CommandManager commandManager;
    private final CommandsConfig commands;

    @Inject
    public CommandRegistrar(Injector injector, CommandManager commandManager, CommandsConfig commands) {
        this.injector = injector;
        this.commandManager = commandManager;
        this.commands = commands;
    }

    public void registerCommands() {
        commandManager.registerCommand(injector.getInstance(MainCommand.class));
        commandManager.registerCommand(injector.getInstance(ReloadCommand.class));
        commandManager.registerCommand(injector.getInstance(PrivateMessageCommand.class));

        for (me.whereareiam.socialismus.model.commandmessaging.CommandMessaging rpCommand : commands.commandMessaging) {
            if (rpCommand.enabled) {
                CommandMessagingTemplate command = injector.getInstance(CommandMessagingTemplate.class);
                command.setRolePlay(rpCommand);
                commandManager.registerCommand(command);
            }
        }
    }
}
