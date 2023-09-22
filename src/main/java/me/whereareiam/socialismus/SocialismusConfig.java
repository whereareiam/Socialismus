package me.whereareiam.socialismus;

import com.google.inject.AbstractModule;
import me.whereareiam.socialismus.command.manager.CommandManager;
import me.whereareiam.socialismus.config.command.CommandsConfig;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.integration.IntegrationManager;
import me.whereareiam.socialismus.util.FormatterUtil;
import me.whereareiam.socialismus.util.InfoPrinterUtil;
import me.whereareiam.socialismus.util.LoggerUtil;
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
        bind(FormatterUtil.class).asEagerSingleton();

        bind(CommandManager.class).asEagerSingleton();
        bind(IntegrationManager.class).asEagerSingleton();

        bind(LoggerUtil.class).asEagerSingleton();
        bind(InfoPrinterUtil.class).asEagerSingleton();
    }
}