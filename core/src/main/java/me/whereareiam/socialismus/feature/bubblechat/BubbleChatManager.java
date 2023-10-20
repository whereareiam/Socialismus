package me.whereareiam.socialismus.feature.bubblechat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.feature.bubblechat.BubbleChatConfig;
import me.whereareiam.socialismus.feature.Feature;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;

@Singleton
public class BubbleChatManager implements Feature {
    private final LoggerUtil loggerUtil;
    private final Path featureFolder;
    private final BubbleChatConfig bubbleChatConfig;

    @Inject
    public BubbleChatManager(LoggerUtil loggerUtil, Plugin plugin, BubbleChatConfig bubbleChatConfig) {
        this.loggerUtil = loggerUtil;
        this.featureFolder = plugin.getDataFolder().toPath().resolve("features");
        this.bubbleChatConfig = bubbleChatConfig;

        loggerUtil.trace("Initializing class: " + this);

        bubbleChatConfig.reload(featureFolder.resolve("bubblechat.yml"));
    }

    @Override
    public boolean requiresChatListener() {
        return true;
    }

    @Override
    public boolean requiresJoinListener() {
        return false;
    }
}
