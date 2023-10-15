package me.whereareiam.socialismus.event;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.message.ChatMessage;
import me.whereareiam.socialismus.chat.message.ChatMessageDistributor;
import me.whereareiam.socialismus.chat.message.ChatMessageFactory;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.entity.Player;

@Singleton
public class ChatEventHandler {
    private final LoggerUtil loggerUtil;
    private final ChatMessageDistributor chatMessageDistributor;
    private final ChatMessageFactory chatMessageFactory;

    @Inject
    public ChatEventHandler(LoggerUtil loggerUtil, ChatMessageDistributor chatMessageDistributor, ChatMessageFactory chatMessageFactory) {
        this.loggerUtil = loggerUtil;
        this.chatMessageDistributor = chatMessageDistributor;
        this.chatMessageFactory = chatMessageFactory;
    }

    public void handleChatEvent(Player player, String message) {
        loggerUtil.debug("Handling chat event for " + player.getName());
        ChatMessage chatMessage = chatMessageFactory.createChatMessage(player, message);

        if (chatMessage.chat() != null) {
            loggerUtil.trace("Found chat for message: " + chatMessage.chat().id);
            chatMessageDistributor.distributeMessage(chatMessage);
        }
    }
}

