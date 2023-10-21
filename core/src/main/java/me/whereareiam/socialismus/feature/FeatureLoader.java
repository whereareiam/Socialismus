package me.whereareiam.socialismus.feature;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
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
    private final SettingsConfig settingsConfig;
    private final File dataFolder;

    private final ChatManager chatManager;
    private final SwapperManager swapperManager;

    private final ChatListenerState chatListenerState;
    private final JoinListenerState joinListenerState;

    @Inject
    public FeatureLoader(LoggerUtil loggerUtil,
                         Plugin plugin, SettingsConfig settingsConfig,

                         ChatManager chatManager, SwapperManager swapperManager,

                         ChatListenerState chatListenerState,
                         JoinListenerState joinListenerState
    ) {
        this.loggerUtil = loggerUtil;
        this.settingsConfig = settingsConfig;
        this.dataFolder = plugin.getDataFolder();

        this.chatManager = chatManager;
        this.swapperManager = swapperManager;

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

        if (settingsConfig.features.chats) {
            chatListenerState.setChatListenerRequired(true);
            chatManager.registerChats();
        }

        if (settingsConfig.features.swapper.enabled) {
            chatListenerState.setChatListenerRequired(true);
            if (settingsConfig.features.swapper.suggest) {
                joinListenerState.setJoinListenerRequired(true);
            }

            swapperManager.registerSwappers();
        }

        if (settingsConfig.features.bubblechat) {
            chatListenerState.setChatListenerRequired(true);
        }
    }

    public void reloadFeatures() {
        loggerUtil.debug("Reloading features");

        if (settingsConfig.features.chats) {
            chatManager.cleanChats();
            chatManager.registerChats();
        }

        if (settingsConfig.features.swapper.enabled) {
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
