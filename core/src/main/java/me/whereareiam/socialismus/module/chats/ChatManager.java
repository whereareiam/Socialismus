package me.whereareiam.socialismus.module.chats;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.cache.Cacheable;
import me.whereareiam.socialismus.config.module.chat.ChatsConfig;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.module.Module;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class ChatManager implements Module {
    private final LoggerUtil loggerUtil;
    private final ChatsConfig chatsConfig;
    private final Path modulePath;

    private final Map<String, Chat> chats = new HashMap<>();

    @Inject
    public ChatManager(LoggerUtil loggerUtil, Plugin plugin, ChatsConfig chatsConfig) {
        this.loggerUtil = loggerUtil;
        this.chatsConfig = chatsConfig;
        this.modulePath = plugin.getDataFolder().toPath().resolve("modules");

        loggerUtil.trace("Initializing class: " + this);
    }

    public void registerChat(Chat chat) {
        loggerUtil.trace("Registering chat: " + chat.id);
        chats.put(chat.id, chat);
    }

    public void registerChats() {
        chatsConfig.reload(modulePath.resolve("chats.yml"));

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
