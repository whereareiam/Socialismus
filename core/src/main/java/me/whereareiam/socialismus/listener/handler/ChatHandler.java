package me.whereareiam.socialismus.listener.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.ChatService;
import me.whereareiam.socialismus.chat.message.ChatMessage;
import me.whereareiam.socialismus.chat.message.ChatMessageFactory;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.feature.bubblechat.BubbleChatService;
import org.bukkit.entity.Player;

@Singleton
public class ChatHandler {
    private final ChatMessageFactory chatMessageFactory;
    private final SettingsConfig settingsConfig;
    private final ChatService chatService;
    private final BubbleChatService bubbleChatService;

    @Inject
    public ChatHandler(ChatMessageFactory chatMessageFactory, SettingsConfig settingsConfig,
                       ChatService chatService, BubbleChatService bubbleChatService) {
        this.chatMessageFactory = chatMessageFactory;
        this.settingsConfig = settingsConfig;

        this.chatService = chatService;
        this.bubbleChatService = bubbleChatService;
    }

    public void handleChatEvent(Player player, String message) {
        ChatMessage chatMessage = chatMessageFactory.createChatMessage(player, message);

        if (settingsConfig.features.bubblechat) {
            bubbleChatService.distributeBubbleMessage(chatMessage);
        }

        if (settingsConfig.features.chats && chatMessage.getChat() != null) {
            chatService.distributeMessage(chatMessage);
        }
    }
}

