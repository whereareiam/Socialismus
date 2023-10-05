package me.whereareiam.socialismus.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.whereareiam.socialismus.event.ChatEventHandler;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

@Singleton
public class AsyncChatListener implements PlayerChatListener {
    private final ChatEventHandler chatEventHandler;

    @Inject
    public AsyncChatListener(ChatEventHandler chatEventHandler) {
        this.chatEventHandler = chatEventHandler;
    }

    @EventHandler
    public void onEvent(AsyncChatEvent event) {
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        ;

        onPlayerChatEvent(event.getPlayer(), message);
        event.setCancelled(true);
    }

    @Override
    public void onPlayerChatEvent(Player player, String message) {
        chatEventHandler.handleChatEvent(player, message);
    }
}
