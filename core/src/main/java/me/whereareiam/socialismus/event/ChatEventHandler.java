package me.whereareiam.socialismus.event;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.feature.chat.ChatHandler;
import me.whereareiam.socialismus.feature.chat.message.ChatMessage;
import me.whereareiam.socialismus.feature.chat.message.ChatMessageFactory;
import org.bukkit.entity.Player;

@Singleton
public class ChatEventHandler {
    private final ChatHandler chatHandler;
    private final ChatMessageFactory chatMessageFactory;

    @Inject
    public ChatEventHandler(ChatHandler chatHandler, ChatMessageFactory chatMessageFactory) {
        this.chatHandler = chatHandler;
        this.chatMessageFactory = chatMessageFactory;
    }

    public void handleChatEvent(Player player, String message) {
        ChatMessage chatMessage = chatMessageFactory.createChatMessage(player, message);

        if (chatMessage.chat() != null) {
            chatHandler.handleChat(chatMessage);
        }
    }
}

