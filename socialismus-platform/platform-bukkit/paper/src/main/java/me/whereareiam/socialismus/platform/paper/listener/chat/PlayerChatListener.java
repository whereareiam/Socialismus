package me.whereareiam.socialismus.platform.paper.listener.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.input.sync.ChatSyncBus;
import me.whereareiam.socialismus.api.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.api.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.api.output.listener.DynamicListener;
import me.whereareiam.socialismus.common.chat.ChatCoordinator;
import me.whereareiam.socialismus.common.chat.ChatMessageFactory;
import me.whereareiam.socialismus.common.chat.broadcast.ChatBroadcaster;
import me.whereareiam.socialismus.platform.paper.renderer.SocialismusRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.stream.Collectors;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PlayerChatListener implements DynamicListener<AsyncChatEvent> {
	private final ChatSyncBus chatSyncBus;
	private final ChatCoordinator chatCoordinator;
	private final ChatMessageFactory chatMessageFactory;
	private final ChatBroadcaster chatBroadcaster;

	public void onEvent(AsyncChatEvent event) {
		Player player = event.getPlayer();
		Set<Audience> recipients = event.viewers();
		Component content = event.message();

		ChatMessage chatMessage = chatMessageFactory.createChatMessage(
				player.getUniqueId(),
				recipients.stream()
						.filter(c -> !(c.getClass().getName().equals("com.destroystokyo.paper.console.TerminalConsoleCommandSender")))
						.map(audience -> ((Player) audience).getUniqueId())
						.collect(Collectors.toSet()),
				content
		);
		chatSyncBus.publish(chatMessage);
		FormattedChatMessage formattedChatMessage = chatCoordinator.coordinate(chatMessage);

		if (formattedChatMessage == null || formattedChatMessage.isCancelled() || !formattedChatMessage.isVanillaSending()) {
			event.setCancelled(true);
			return;
		}

		event.viewers().clear();
		event.viewers().addAll(
				formattedChatMessage.getRecipients().stream()
						.map(recipient -> player.getServer().getPlayer(recipient.getUniqueId()))
						.collect(Collectors.toSet())
		);

		event.renderer(new SocialismusRenderer(formattedChatMessage, chatBroadcaster));
	}
}
