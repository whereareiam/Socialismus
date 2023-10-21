package me.whereareiam.socialismus.listener.handler;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.chat.ChatService;
import me.whereareiam.socialismus.chat.message.ChatMessage;
import me.whereareiam.socialismus.chat.message.ChatMessageFactory;
import me.whereareiam.socialismus.config.setting.SettingsConfig;
import me.whereareiam.socialismus.feature.bubblechat.BubbleChatService;
import org.bukkit.entity.Player;

@Singleton
public class ChatHandler {
    private final Injector injector;
    private final ChatMessageFactory chatMessageFactory;
    private final SettingsConfig settingsConfig;

    @Inject
    public ChatHandler(Injector injector, ChatMessageFactory chatMessageFactory, SettingsConfig settingsConfig) {
        this.injector = injector;
        this.chatMessageFactory = chatMessageFactory;
        this.settingsConfig = settingsConfig;
    }

    public boolean handleChatEvent(Player player, String message) {
        boolean cancelEvent = false;
        ChatMessage chatMessage = chatMessageFactory.createChatMessage(player, message);

        if (settingsConfig.features.bubblechat) {
            final BubbleChatService bubbleChatService = injector.getInstance(BubbleChatService.class);
            bubbleChatService.distributeBubbleMessage(chatMessage);
        }

        if (settingsConfig.features.chats && chatMessage.getChat() != null) {
            cancelEvent = true;

            final ChatService chatService = injector.getInstance(ChatService.class);
            chatService.distributeMessage(chatMessage);
        }

        return cancelEvent;
    }
}

