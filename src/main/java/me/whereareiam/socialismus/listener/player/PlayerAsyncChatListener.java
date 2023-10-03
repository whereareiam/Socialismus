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
        char chatSymbol = message.charAt(0);
        String symbol;

        if (!Character.isLetterOrDigit(chatSymbol)) {
            symbol = String.valueOf(chatSymbol);
        } else {
            symbol = "";
        }

        Chat chat = chatManager.getChatBySymbol(symbol);

        if (chat != null) {
            loggerUtil.info("Chat: " + chat.id + " Message: " + event.getMessage());
        }
    }
}
