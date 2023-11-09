package me.whereareiam.socialismus.command.management;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.command.commands.CommandMessagingCommand;
import me.whereareiam.socialismus.command.commands.MainCommand;
import me.whereareiam.socialismus.config.command.CommandsConfig;
import me.whereareiam.socialismus.model.commandmessaging.CommandMessaging;

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
        commandManager.registerCommand(injector.getInstance(MainCommand.class));
        commandManager.registerCommand(injector.getInstance(MainCommand.class));

        for (CommandMessaging rpCommand : commands.commandMessaging) {
            if (rpCommand.enabled) {
                CommandMessagingCommand command = injector.getInstance(CommandMessagingCommand.class);
                command.setRolePlay(rpCommand);
                commandManager.registerCommand(command);
            }
        }
    }
}
