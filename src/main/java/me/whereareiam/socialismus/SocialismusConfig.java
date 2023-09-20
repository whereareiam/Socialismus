package me.whereareiam.socialismus;

import com.google.inject.AbstractModule;
import me.whereareiam.socialismus.command.manager.CommandManager;
import me.whereareiam.socialismus.config.CommandsConfig;
import me.whereareiam.socialismus.config.MessagesConfig;
import me.whereareiam.socialismus.config.SettingsConfig;
import me.whereareiam.socialismus.util.InfoPrinter;
import me.whereareiam.socialismus.util.Logger;
import org.bukkit.plugin.Plugin;

public class SocialismusConfig extends AbstractModule {
    private final Plugin plugin;

    public SocialismusConfig(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(Plugin.class).toInstance(plugin);

        bind(SettingsConfig.class).asEagerSingleton();
        bind(MessagesConfig.class).asEagerSingleton();
        bind(CommandsConfig.class).asEagerSingleton();

        bind(Logger.class).asEagerSingleton();
        bind(InfoPrinter.class).asEagerSingleton();
        bind(CommandManager.class).asEagerSingleton();
    }
}