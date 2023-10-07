package me.whereareiam.socialismus.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.event.ChatEventHandler;
import me.whereareiam.socialismus.util.LoggerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@Singleton
public class PlayerAsyncChatListener implements PlayerChatListener {
    private final LoggerUtil loggerUtil;
    private final ChatEventHandler chatEventHandler;

    @Inject
    public PlayerAsyncChatListener(LoggerUtil loggerUtil, ChatEventHandler chatEventHandler) {
        this.loggerUtil = loggerUtil;
        this.chatEventHandler = chatEventHandler;

        loggerUtil.trace("Initializing class: " + this);
    }

    @EventHandler
    public void onEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        loggerUtil.debug("AsyncPlayerChatEvent: " + player.getName() + " " + message);

        onPlayerChatEvent(player, message);
        event.setCancelled(true);
    }

    @Override
    public void onPlayerChatEvent(Player player, String message) {
        chatEventHandler.handleChatEvent(player, message);
    }
}