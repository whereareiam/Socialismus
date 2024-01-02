package me.whereareiam.socialismus.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.config.command.CommandsConfig;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.config.setting.SettingsConfig;

import java.nio.file.Path;

@Singleton
public class ConfigManager {
    private final SettingsConfig settings;
    private final MessagesConfig messages;
    private final CommandsConfig commands;
    private final Path pluginPath;

    @Inject
    public ConfigManager(@Named("pluginPath") Path pluginPath, SettingsConfig settings, MessagesConfig messages, CommandsConfig commands) {
        this.settings = settings;
        this.messages = messages;
        this.commands = commands;
        this.pluginPath = pluginPath;
    }

    public void reloadConfigs() {
        settings.reload(pluginPath.resolve("settings.yml"));
        messages.reload(pluginPath.resolve("messages.yml"));
        commands.reload(pluginPath.resolve("commands.yml"));
    }
}