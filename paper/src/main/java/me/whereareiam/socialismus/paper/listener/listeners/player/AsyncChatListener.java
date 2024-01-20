package me.whereareiam.socialismus.paper.listener.listeners.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.whereareiam.socialismus.core.listener.ChatListener;
import me.whereareiam.socialismus.core.listener.handler.ChatHandler;
import me.whereareiam.socialismus.core.util.LoggerUtil;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Collection;
import java.util.stream.Collectors;

@Singleton
public class AsyncChatListener implements ChatListener {
	private final ChatHandler chatHandler;

	@Inject
	public AsyncChatListener(LoggerUtil loggerUtil, ChatHandler chatHandler) {
		this.chatHandler = chatHandler;

		loggerUtil.trace("Initializing class: " + this);
	}

	@EventHandler
	public void onEvent(AsyncChatEvent event) {
		Player player = event.getPlayer();
		Collection<? extends Player> recipients = event.viewers().stream()
				.filter(audience -> audience instanceof Player)
				.map(audience -> (Player) audience)
				.collect(Collectors.toList());
		String message = PlainTextComponentSerializer.plainText().serialize(event.message());

		boolean cancelEvent = onPlayerChatEvent(player, recipients, message);
		event.setCancelled(cancelEvent);
	}

	@Override
	public boolean onPlayerChatEvent(Player player, Collection<? extends Player> recipients, String message) {
		return chatHandler.handleChatEvent(player, recipients, message);
	}
}
