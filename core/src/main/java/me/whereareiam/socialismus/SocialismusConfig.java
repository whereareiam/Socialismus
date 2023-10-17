package me.whereareiam.socialismus;

import co.aikar.commands.BukkitCommandManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;
import me.whereareiam.socialismus.cache.CacheInterceptor;
import me.whereareiam.socialismus.cache.Cacheable;
import me.whereareiam.socialismus.chat.message.ChatMessageProcessor;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.feature.swapper.SwapperService;
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
        bind(ProtocolManager.class).toInstance(ProtocolLibrary.getProtocolManager());
        bind(BukkitCommandManager.class).toInstance(new BukkitCommandManager(plugin));

        SettingsConfig settingsConfig = new SettingsConfig();
        bind(SettingsConfig.class).toInstance(settingsConfig);

        LoggerUtil loggerUtil = new LoggerUtil(settingsConfig);
        loggerUtil.setBukkitLogger(plugin.getLogger());
        bind(LoggerUtil.class).toInstance(loggerUtil);
        
        Multibinder<ChatMessageProcessor> chatMessageProcessorMultibinder = Multibinder.newSetBinder(binder(), ChatMessageProcessor.class);
        chatMessageProcessorMultibinder.addBinding().to(SwapperService.class);

        bindInterceptor(Matchers.any(),
                Matchers.annotatedWith(Cacheable.class),
                new CacheInterceptor(settingsConfig, loggerUtil)
        );
    }
}