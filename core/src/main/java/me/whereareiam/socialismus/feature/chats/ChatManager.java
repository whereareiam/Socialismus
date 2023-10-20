package me.whereareiam.socialismus.feature.chats;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.cache.Cacheable;
import me.whereareiam.socialismus.chat.model.Chat;
import me.whereareiam.socialismus.config.feature.chat.ChatsConfig;
import me.whereareiam.socialismus.feature.Feature;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class ChatManager implements Feature {
    private final LoggerUtil loggerUtil;
    private final ChatsConfig chatsConfig;
    private final Path featureFolder;

    private final Map<String, Chat> chats = new HashMap<>();

    @Inject
    public ChatManager(LoggerUtil loggerUtil, Plugin plugin, ChatsConfig chatsConfig) {
        this.loggerUtil = loggerUtil;
        this.chatsConfig = chatsConfig;
        this.featureFolder = plugin.getDataFolder().toPath().resolve("features");

        loggerUtil.trace("Initializing class: " + this);
    }

    public void registerChat(Chat chat) {
        loggerUtil.trace("Registering chat: " + chat.id);
        chats.put(chat.id, chat);
    }

    public void registerChats() {
        chatsConfig.reload(featureFolder.resolve("chats.yml"));

        for (Chat chat : chatsConfig.chats) {
            registerChat(chat);
        }
    }

    public void cleanChats() {
        chats.clear();
        loggerUtil.trace("All chats have been cleaned");
    }

    @Cacheable(duration = 5)
    public Chat getChatBySymbol(String symbol) {
        for (Chat chat : chats.values()) {
            if (chat.chatSymbol.equals(symbol)) {
                return chat;
            }
        }
        return null;
    }

    public int getChatCount() {
        return chats.size();
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
