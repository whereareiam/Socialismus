package me.whereareiam.socialismus.feature;

import com.google.inject.Inject;
import com.google.inject.Injector;
import me.whereareiam.socialismus.config.chat.ChatsConfig;
import me.whereareiam.socialismus.config.setting.FeaturesConfig;
import me.whereareiam.socialismus.feature.chat.Chat;
import me.whereareiam.socialismus.feature.chat.ChatManager;
import me.whereareiam.socialismus.service.ChatService;
import org.bukkit.plugin.Plugin;

public class FeatureLoader {
    private final Injector injector;
    private final Plugin plugin;
    private final FeaturesConfig featuresConfig;
    private final ChatManager chatManager;

    private final ChatService chatService;

    @Inject
    public FeatureLoader(Injector injector, Plugin plugin, FeaturesConfig featuresConfig,
                         ChatManager chatManager, ChatService chatService
    ) {
        this.injector = injector;
        this.plugin = plugin;
        this.featuresConfig = featuresConfig;
        this.chatManager = chatManager;

        this.chatService = chatService;
    }

    public void loadFeatures() {
        if (featuresConfig.chats) {
            chatService.setChatListenerRequired(true);

            ChatsConfig chatsConfig = injector.getInstance(ChatsConfig.class);
            chatsConfig.reload(plugin.getDataFolder().toPath().resolve("chats.yml"));

            for (Chat chat : chatsConfig.chats) {
                chatManager.registerChat(chat);
            }
        }
    }

    public int getChatCount() {
        return chatManager.getChatCount();
    }
}
