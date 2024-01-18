package me.whereareiam.socialismus.paper.listener.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.whereareiam.socialismus.core.listener.ChatListener;
import me.whereareiam.socialismus.core.listener.handler.ChatHandler;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

@Singleton
public class AsyncChatListener implements ChatListener {
	private final LoggerUtil loggerUtil;
	private final ChatHandler chatHandler;

	@Inject
	public AsyncChatListener(LoggerUtil loggerUtil, ChatHandler chatHandler) {
		this.loggerUtil = loggerUtil;
		this.chatHandler = chatHandler;

		loggerUtil.trace("Initializing class: " + this);
	}

	@EventHandler
	public void onEvent(AsyncChatEvent event) {
		Player player = event.getPlayer();
		String message = PlainTextComponentSerializer.plainText().serialize(event.message());

		loggerUtil.debug("AsyncChatEvent: " + player.getName() + " " + message);

		boolean cancelEvent = onPlayerChatEvent(player, message);
		event.setCancelled(cancelEvent);
	}

	@Override
	public boolean onPlayerChatEvent(Player player, String message) {
		return chatHandler.handleChatEvent(player, message);
	}
}
