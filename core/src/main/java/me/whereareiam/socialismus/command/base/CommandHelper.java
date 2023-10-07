package me.whereareiam.socialismus.command.base;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.locales.MessageKey;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Locale;

@Singleton
public class CommandHelper {
    private final BukkitCommandManager bukkitCommandManager;

    @Inject
    public CommandHelper(BukkitCommandManager bukkitCommandManager) {
        this.bukkitCommandManager = bukkitCommandManager;
    }

    public void addReplacement(String message, String replacementId) {
        bukkitCommandManager.getCommandReplacements().addReplacement(replacementId, message);
    }

    public void addTranslation(String message, String acfKey) {
        bukkitCommandManager.getLocales().addMessage(Locale.ENGLISH, MessageKey.of(acfKey), message);
    }
}

