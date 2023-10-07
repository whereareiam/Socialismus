package me.whereareiam.socialismus.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.whereareiam.socialismus.event.ChatEventHandler;
import me.whereareiam.socialismus.util.LoggerUtil;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

@Singleton
public class AsyncChatListener implements PlayerChatListener {
    private final LoggerUtil loggerUtil;
    private final ChatEventHandler chatEventHandler;

    @Inject
    public AsyncChatListener(LoggerUtil loggerUtil, ChatEventHandler chatEventHandler) {
        this.loggerUtil = loggerUtil;
        this.chatEventHandler = chatEventHandler;

        loggerUtil.trace("Initializing class: " + this);
    }

    @EventHandler
    public void onEvent(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());

        loggerUtil.debug("AsyncChatEvent: " + player.getName() + " " + message);

        onPlayerChatEvent(player, message);
        event.setCancelled(true);
    }

    @Override
    public void onPlayerChatEvent(Player player, String message) {
        chatEventHandler.handleChatEvent(player, message);
    }
}
