package me.whereareiam.socialismus.feature;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.setting.FeaturesSettingsConfig;
import me.whereareiam.socialismus.feature.bubblechat.BubbleChatManager;
import me.whereareiam.socialismus.feature.chats.ChatManager;
import me.whereareiam.socialismus.feature.swapper.SwapperManager;
import me.whereareiam.socialismus.listener.state.ChatListenerState;
import me.whereareiam.socialismus.listener.state.JoinListenerState;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.plugin.Plugin;

import java.io.File;

@Singleton
public class FeatureLoader {
    private final LoggerUtil loggerUtil;
    private final FeaturesSettingsConfig featuresSettingsConfig;
    private final File dataFolder;

    private final ChatManager chatManager;
    private final SwapperManager swapperManager;
    private final BubbleChatManager bubbleChatManager;

    private final ChatListenerState chatListenerState;
    private final JoinListenerState joinListenerState;

    @Inject
    public FeatureLoader(LoggerUtil loggerUtil,
                         Plugin plugin, FeaturesSettingsConfig featuresSettingsConfig,

                         ChatManager chatManager, SwapperManager swapperManager,
                         BubbleChatManager bubbleChatManager,

                         ChatListenerState chatListenerState,
                         JoinListenerState joinListenerState
    ) {
        this.loggerUtil = loggerUtil;
        this.featuresSettingsConfig = featuresSettingsConfig;
        this.dataFolder = plugin.getDataFolder();

        this.chatManager = chatManager;
        this.swapperManager = swapperManager;
        this.bubbleChatManager = bubbleChatManager;

        this.chatListenerState = chatListenerState;
        this.joinListenerState = joinListenerState;

        loggerUtil.trace("Initializing class: " + this);
    }

    public void loadFeatures() {
        loggerUtil.debug("Loading features");
        File featureFolder = dataFolder.toPath().resolve("features").toFile();

        if (!featureFolder.exists()) {
            boolean isCreated = featureFolder.mkdir();
            loggerUtil.debug("Creating feature dir");
            if (!isCreated) {
                loggerUtil.severe("Failed to create directory: " + featureFolder);
            }
        }

        if (featuresSettingsConfig.chats) {
            chatListenerState.setChatListenerRequired(true);
            chatManager.registerChats();
        }

        if (featuresSettingsConfig.swapper.enabled) {
            chatListenerState.setChatListenerRequired(true);
            if (featuresSettingsConfig.swapper.suggest) {
                joinListenerState.setJoinListenerRequired(true);
            }

            swapperManager.registerSwappers();
        }
    }

    public void reloadFeatures() {
        loggerUtil.debug("Reloading features");

        if (featuresSettingsConfig.chats) {
            chatManager.cleanChats();
            chatManager.registerChats();
        }

        if (featuresSettingsConfig.swapper.enabled) {
            swapperManager.cleanSwappers();
            swapperManager.registerSwappers();
        }
    }

    public int getChatCount() {
        return chatManager.getChatCount();
    }

    public int getSwapperCount() {
        return swapperManager.getSwappers().size();
    }
}
