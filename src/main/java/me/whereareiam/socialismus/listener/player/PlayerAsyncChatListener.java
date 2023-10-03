package me.whereareiam.socialismus.listener.player;

import com.google.inject.Inject;
import me.whereareiam.socialismus.feature.chat.ChatHandler;
import me.whereareiam.socialismus.feature.chat.message.ChatMessage;
import me.whereareiam.socialismus.feature.chat.message.ChatMessageFactory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerAsyncChatListener implements Listener {
    private final ChatMessageFactory chatMessageFactory;
    private final ChatHandler chatHandler;

    @Inject
    public PlayerAsyncChatListener(ChatMessageFactory chatMessageFactory, ChatHandler chatHandler) {
        this.chatMessageFactory = chatMessageFactory;
        this.chatHandler = chatHandler;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        ChatMessage chatMessage = chatMessageFactory.createChatMessage(event.getPlayer(), event.getMessage());

        if (chatMessage.chat().id != null) {
            chatHandler.handleChat(chatMessage);
            event.setCancelled(true);
        }
    }
}
