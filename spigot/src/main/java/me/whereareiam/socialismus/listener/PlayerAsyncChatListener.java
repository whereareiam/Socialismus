package me.whereareiam.socialismus.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.event.ChatEventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@Singleton
public class PlayerAsyncChatListener implements PlayerChatListener {
    private final ChatEventHandler chatEventHandler;

    @Inject
    public PlayerAsyncChatListener(ChatEventHandler chatEventHandler) {
        this.chatEventHandler = chatEventHandler;
    }

    @EventHandler
    public void onEvent(AsyncPlayerChatEvent event) {
        onPlayerChatEvent(event.getPlayer(), event.getMessage());
        event.setCancelled(true);
    }

    @Override
    public void onPlayerChatEvent(Player player, String message) {
        chatEventHandler.handleChatEvent(player, message);
    }
}