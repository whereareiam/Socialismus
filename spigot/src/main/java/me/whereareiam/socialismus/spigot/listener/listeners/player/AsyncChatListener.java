package me.whereareiam.socialismus.spigot.listener.listeners.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.core.listener.ChatListener;
import me.whereareiam.socialismus.core.listener.handler.ChatHandler;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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
	public void onEvent(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();

		loggerUtil.debug("AsyncPlayerChatEvent: " + player.getName() + " " + message);

		boolean cancelEvent = onPlayerChatEvent(player, message);
		event.setCancelled(cancelEvent);
	}

	@Override
	public boolean onPlayerChatEvent(Player player, String message) {
		return chatHandler.handleChatEvent(player, message);
	}
}