package me.whereareiam.socialismus.platform.velocity.listener.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.api.output.listener.DynamicListener;
import me.whereareiam.socialismus.common.chat.ChatCoordinator;
import me.whereareiam.socialismus.common.chat.ChatMessageFactory;
import net.kyori.adventure.text.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PlayerChatListener implements DynamicListener<PlayerChatEvent> {
	private final ProxyServer proxyServer;
	private final ChatCoordinator chatCoordinator;
	private final ChatMessageFactory chatMessageFactory;

	public void onEvent(PlayerChatEvent event) {
		Player player = event.getPlayer();
		Collection<Player> recipients = proxyServer.getAllPlayers();
		Component content = Component.text(event.getMessage());

		ChatMessage chatMessage = chatMessageFactory.createChatMessage(
				player.getUniqueId(),
				recipients.stream().map(Player::getUniqueId).collect(Collectors.toSet()),
				content
		);

		chatCoordinator.coordinate(chatMessage);

		event.setResult(PlayerChatEvent.ChatResult.denied());
	}
}
