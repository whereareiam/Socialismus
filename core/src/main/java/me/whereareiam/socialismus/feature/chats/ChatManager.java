package me.whereareiam.socialismus.feature.chats;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.cache.Cacheable;
import me.whereareiam.socialismus.chat.Chat;
import me.whereareiam.socialismus.feature.Feature;
import me.whereareiam.socialismus.util.LoggerUtil;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class ChatManager implements Feature {
    private final LoggerUtil loggerUtil;
    private final Map<String, Chat> chats = new HashMap<>();

    @Inject
    public ChatManager(LoggerUtil loggerUtil) {
        this.loggerUtil = loggerUtil;

        loggerUtil.trace("Initializing class: " + this);
    }

    public void registerChat(Chat chat) {
        loggerUtil.trace("Registering chat: " + chat.id);
        chats.put(chat.id, chat);
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
    public boolean requiresPlayerAsyncChatListener() {
        return true;
    }
}
