package me.whereareiam.socialismus.feature.chat;

import jakarta.inject.Singleton;
import me.whereareiam.socialismus.feature.Feature;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class ChatManager implements Feature {
    private final Map<String, Chat> chats = new HashMap<>();

    public void registerChat(Chat chat) {
        chats.put(chat.id, chat);
    }

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
