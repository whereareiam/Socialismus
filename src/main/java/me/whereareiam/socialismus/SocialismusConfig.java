package me.whereareiam.socialismus;

import co.aikar.commands.BukkitCommandManager;
import com.google.inject.AbstractModule;
import me.whereareiam.socialismus.command.management.CommandManager;
import me.whereareiam.socialismus.command.management.CommandRegistrar;
import me.whereareiam.socialismus.config.command.CommandsConfig;
import me.whereareiam.socialismus.config.message.MessagesConfig;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.feature.FeatureLoader;
import me.whereareiam.socialismus.integration.IntegrationManager;
import me.whereareiam.socialismus.listener.ListenerRegistrar;
import me.whereareiam.socialismus.service.player.PlayerAsyncChatService;
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

        bind(BukkitCommandManager.class).toInstance(new BukkitCommandManager(plugin));
        bind(CommandManager.class).asEagerSingleton();
        bind(CommandRegistrar.class).asEagerSingleton();
        bind(IntegrationManager.class).asEagerSingleton();
        bind(FormatterUtil.class).asEagerSingleton();

        //Services
        bind(PlayerAsyncChatService.class).asEagerSingleton();

        bind(FeatureLoader.class).asEagerSingleton();
        bind(ListenerRegistrar.class).asEagerSingleton();

        bind(LoggerUtil.class).asEagerSingleton();
        bind(InfoPrinterUtil.class).asEagerSingleton();
    }
}