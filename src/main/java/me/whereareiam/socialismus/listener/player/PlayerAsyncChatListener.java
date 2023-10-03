package me.whereareiam.socialismus.listener.player;

import com.google.inject.Inject;
import me.whereareiam.socialismus.feature.chat.Chat;
import me.whereareiam.socialismus.feature.chat.ChatManager;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerAsyncChatListener implements Listener {
    private final ChatManager chatManager;
    private final LoggerUtil loggerUtil;

    @Inject
    public PlayerAsyncChatListener(ChatManager chatManager, LoggerUtil loggerUtil) {
        this.chatManager = chatManager;
        this.loggerUtil = loggerUtil;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        String chatSymbol = message.substring(0, 1);

        Chat chat = chatManager.getChatBySymbol(chatSymbol);

        if (chat != null) {
            loggerUtil.info("Chat: " + chat + " Message: " + event.getMessage());
        }
    }
}
