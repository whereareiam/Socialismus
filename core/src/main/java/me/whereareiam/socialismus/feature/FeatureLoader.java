package me.whereareiam.socialismus.feature;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.cache.Cacheable;
import me.whereareiam.socialismus.chat.model.Chat;
import me.whereareiam.socialismus.config.chat.ChatsConfig;
import me.whereareiam.socialismus.config.setting.FeaturesSettingsConfig;
import me.whereareiam.socialismus.feature.chats.ChatManager;
import me.whereareiam.socialismus.feature.swapper.SwapperManager;
import me.whereareiam.socialismus.service.ChatService;
import me.whereareiam.socialismus.service.TabCompleteService;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.plugin.Plugin;

@Singleton
public class FeatureLoader {
    private final Injector injector;
    private final LoggerUtil loggerUtil;
    private final FeaturesSettingsConfig featuresSettingsConfig;
    private final Plugin plugin;

    private final ChatManager chatManager;
    private final SwapperManager swapperManager;

    private final ChatService chatService;
    private final TabCompleteService tabCompleteService;

    @Inject
    public FeatureLoader(Injector injector, LoggerUtil loggerUtil,
                         Plugin plugin, FeaturesSettingsConfig featuresSettingsConfig,

                         ChatManager chatManager, SwapperManager swapperManager,

                         ChatService chatService, TabCompleteService tabCompleteService
    ) {
        this.injector = injector;
        this.loggerUtil = loggerUtil;
        this.featuresSettingsConfig = featuresSettingsConfig;
        this.plugin = plugin;

        this.chatManager = chatManager;
        this.swapperManager = swapperManager;

        this.chatService = chatService;
        this.tabCompleteService = tabCompleteService;

        loggerUtil.trace("Initializing class: " + this);
    }

    public void loadFeatures() {
        loggerUtil.debug("Loading features");

        if (featuresSettingsConfig.chats) {
            chatService.setChatListenerRequired(true);

            ChatsConfig chatsConfig = injector.getInstance(ChatsConfig.class);
            chatsConfig.reload(plugin.getDataFolder().toPath().resolve("chats.yml"));

            for (Chat chat : chatsConfig.chats) {
                chatManager.registerChat(chat);
            }
        }

        if (featuresSettingsConfig.swapper) {
            chatService.setChatListenerRequired(true);
            tabCompleteService.setTabCompleteListenerRequired(true);

            swapperManager.registerSwappers();
        }
    }

    public void reloadFeatures() {
        chatManager.cleanChats();

        loggerUtil.debug("Reloading features");
        if (featuresSettingsConfig.chats) {
            ChatsConfig chatsConfig = injector.getInstance(ChatsConfig.class);
            for (Chat chat : chatsConfig.chats) {
                chatManager.registerChat(chat);
            }
        }

        if (featuresSettingsConfig.swapper) {
            swapperManager.cleanSwappers();
            swapperManager.registerSwappers();
        }
    }

    @Cacheable
    public int getChatCount() {
        return chatManager.getChatCount();
    }

    @Cacheable
    public int getSwapperCount() {
        return swapperManager.getEnabledSwappersCount();
    }
}
