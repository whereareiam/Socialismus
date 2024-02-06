package me.whereareiam.socialismus.paper.listener.listeners.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.whereareiam.socialismus.api.model.ChatMessage;
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
		String originalMessage = PlainTextComponentSerializer.plainText().serialize(event.message());

		ChatMessage chatMessage = onPlayerChatEvent(player, recipients, originalMessage);

		event.viewers().clear();
		event.viewers().addAll(chatMessage.getRecipients());
		event.message(chatMessage.getContent());
		event.renderer((source, sourceDisplayName, message, viewer) -> chatMessage.getContent());

		event.setCancelled(chatMessage.isCancelled());
	}

	@Override
	public ChatMessage onPlayerChatEvent(Player player, Collection<? extends Player> recipients, String message) {
		return chatHandler.handleChatEvent(player, recipients, message);
	}
}
