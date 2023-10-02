package me.whereareiam.socialismus.feature;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.whereareiam.socialismus.config.chat.ChatsConfig;
import me.whereareiam.socialismus.config.setting.FeaturesConfig;
import org.bukkit.plugin.Plugin;

public class FeatureLoader {
    private final Injector injector;
    private final Plugin plugin;
    private final FeaturesConfig featuresConfig;

    @Inject
    public FeatureLoader(Injector injector, Plugin plugin, FeaturesConfig featuresConfig) {
        this.injector = injector;
        this.plugin = plugin;
        this.featuresConfig = featuresConfig;
    }

    public void loadFeatures() {
        if (featuresConfig.chats) {
            ChatsConfig chatsConfig = injector.getInstance(ChatsConfig.class);

            // TODO Register chats from config & listener

            chatsConfig.reload(plugin.getDataFolder().toPath().resolve("chats.yml"));
        }
    }
}
