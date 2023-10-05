package me.whereareiam.socialismus.command.management;

import com.google.inject.Inject;
import me.whereareiam.socialismus.command.commands.MainCommand;
import me.whereareiam.socialismus.command.commands.ReloadCommand;

public class CommandRegistrar {
    private final CommandManager commandManager;

    @Inject
    public CommandRegistrar(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public void registerCommands() {
        commandManager.registerCommand(MainCommand.class);
        commandManager.registerCommand(ReloadCommand.class);
    }
}