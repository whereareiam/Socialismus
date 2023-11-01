package me.whereareiam.socialismus.module;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.listener.state.ChatListenerState;
import me.whereareiam.socialismus.listener.state.JoinListenerState;
import me.whereareiam.socialismus.module.bubblechat.BubbleChatManager;
import me.whereareiam.socialismus.module.chats.ChatManager;
import me.whereareiam.socialismus.module.swapper.SwapperManager;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.plugin.Plugin;

import java.io.File;

@Singleton
public class ModuleLoader {
    private final Injector injector;
    private final LoggerUtil loggerUtil;
    private final SettingsConfig settingsConfig;
    private final File dataFolder;

    private final ChatListenerState chatListenerState;
    private final JoinListenerState joinListenerState;

    private ChatManager chatManager;
    private SwapperManager swapperManager;

    @Inject
    public ModuleLoader(Injector injector, LoggerUtil loggerUtil,
                        Plugin plugin, SettingsConfig settingsConfig,

                        ChatListenerState chatListenerState,
                        JoinListenerState joinListenerState
    ) {
        this.injector = injector;
        this.loggerUtil = loggerUtil;
        this.settingsConfig = settingsConfig;
        this.dataFolder = plugin.getDataFolder();

        this.chatListenerState = chatListenerState;
        this.joinListenerState = joinListenerState;

        loggerUtil.trace("Initializing class: " + this);
    }

    public void loadModules() {
        loggerUtil.debug("Loading modules");
        File moduleFile = dataFolder.toPath().resolve("modules").toFile();

        if (!moduleFile.exists()) {
            boolean isCreated = moduleFile.mkdir();
            loggerUtil.debug("Creating module dir");
            if (!isCreated) {
                loggerUtil.severe("Failed to create directory: " + moduleFile);
            }
        }

        if (settingsConfig.modules.chats) {
            chatListenerState.setChatListenerRequired(true);

            chatManager = injector.getInstance(ChatManager.class);
            chatManager.registerChats();
        }

        if (settingsConfig.modules.swapper.enabled) {
            chatListenerState.setChatListenerRequired(true);
            if (settingsConfig.modules.swapper.suggest) {
                joinListenerState.setJoinListenerRequired(true);
            }

            swapperManager = injector.getInstance(SwapperManager.class);
            swapperManager.registerSwappers();
        }

        if (settingsConfig.modules.bubblechat) {
            chatListenerState.setChatListenerRequired(true);

            injector.getInstance(BubbleChatManager.class);
        }
    }

    public void reloadModules() {
        loggerUtil.debug("Reloading modules");

        if (settingsConfig.modules.chats) {
            chatManager.cleanChats();
            chatManager.registerChats();
        }

        if (settingsConfig.modules.swapper.enabled) {
            swapperManager.cleanSwappers();
            swapperManager.registerSwappers();
        }
    }

    public int getChatCount() {
        if (chatManager == null)
            return 0;

        return chatManager.getChatCount();
    }

    public int getSwapperCount() {
        if (swapperManager == null)
            return 0;

        return swapperManager.getSwappers().size();
    }
}
