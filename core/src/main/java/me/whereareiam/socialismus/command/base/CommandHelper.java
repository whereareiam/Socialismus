package me.whereareiam.socialismus.command.base;

import co.aikar.commands.BukkitCommandManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CommandHelper {
    private final BukkitCommandManager bukkitCommandManager;

    @Inject
    public CommandHelper(BukkitCommandManager bukkitCommandManager) {
        this.bukkitCommandManager = bukkitCommandManager;
    }

    public void addReplacement(String message, String replacementId) {
        if (message == null)
            message = "";

        bukkitCommandManager.getCommandReplacements().addReplacement(replacementId, message);
    }
}
