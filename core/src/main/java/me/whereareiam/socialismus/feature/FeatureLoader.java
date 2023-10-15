package me.whereareiam.socialismus.feature;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.Chat;
import me.whereareiam.socialismus.config.chat.ChatsConfig;
import me.whereareiam.socialismus.config.setting.FeaturesSettingsConfig;
import me.whereareiam.socialismus.feature.chats.ChatManager;
import me.whereareiam.socialismus.service.ChatService;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.plugin.Plugin;

@Singleton
public class FeatureLoader {
    private final Injector injector;
    private final LoggerUtil loggerUtil;
    private final Plugin plugin;
    private final FeaturesSettingsConfig featuresSettingsConfig;
    private final ChatManager chatManager;

    private final ChatService chatService;

    @Inject
    public FeatureLoader(Injector injector, LoggerUtil loggerUtil,
                         Plugin plugin, FeaturesSettingsConfig featuresSettingsConfig,
                         ChatManager chatManager, ChatService chatService
    ) {
        this.injector = injector;
        this.loggerUtil = loggerUtil;
        this.plugin = plugin;
        this.featuresSettingsConfig = featuresSettingsConfig;
        this.chatManager = chatManager;

        this.chatService = chatService;

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
    }

    public int getChatCount() {
        return chatManager.getChatCount();
    }
}
