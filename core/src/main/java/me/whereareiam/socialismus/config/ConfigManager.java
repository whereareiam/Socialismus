package me.whereareiam.socialismus.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.command.CommandsConfig;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.config.setting.SettingsConfig;

import java.nio.file.Path;

@Singleton
public class ConfigManager {
    private final SettingsConfig settings;
    private final MessagesConfig messages;
    private final CommandsConfig commands;
    private Path dataFolder;

    @Inject
    public ConfigManager(SettingsConfig settings, MessagesConfig messages, CommandsConfig commands) {
        this.settings = settings;
        this.messages = messages;
        this.commands = commands;
    }

    public void setDataFolder(Path dataFolder) {
        this.dataFolder = dataFolder;
    }

    public void reloadConfigs() {
        settings.reload(dataFolder.resolve("settings.yml"));
        messages.reload(dataFolder.resolve("messages.yml"));
        commands.reload(dataFolder.resolve("commands.yml"));
    }
}
