package me.whereareiam.socialismus.platform.bukkit.listener.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.common.chat.ChatCoordinator;
import me.whereareiam.socialismus.common.chat.ChatMessageFactory;
import me.whereareiam.socialismus.listener.DynamicListener;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.service.chat.render.ChatRenderService;
import me.whereareiam.socialismus.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;
import java.util.stream.Collectors;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PlayerChatListener implements DynamicListener<AsyncPlayerChatEvent> {
	private final ChatCoordinator chatCoordinator;
	private final ChatMessageFactory chatMessageFactory;
	private final ChatRenderService chatRenderService;

	public void onEvent(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		Set<Player> recipients = event.getRecipients();
		Component content = Component.text(event.getMessage());

		ChatMessage chatMessage = chatMessageFactory.createChatMessage(
				player.getUniqueId(),
				recipients.stream().map(Entity::getUniqueId).collect(Collectors.toSet()),
				content
		);
		FormattedChatMessage formattedChatMessage = chatCoordinator.coordinate(chatMessage);

		if (formattedChatMessage == null || formattedChatMessage.isCancelled() || !formattedChatMessage.isVanillaSending()) {
			event.setCancelled(true);
			return;
		}

		event.getRecipients().clear();
		event.getRecipients().addAll(
				formattedChatMessage.getRecipients().stream()
						.map(recipient -> player.getServer().getPlayer(recipient.getUniqueId()))
						.collect(Collectors.toSet())
		);

		Component formatComponent = chatRenderService.renderFormat(formattedChatMessage, formattedChatMessage.getSender());
		event.setFormat(ComponentUtil.toLegacy(formatComponent, true).replace(Serializer.placeholder("message"), "%2$s"));

		Component renderedMessage = chatRenderService.renderMessage(formattedChatMessage, formattedChatMessage.getSender());
		event.setMessage(ComponentUtil.toLegacy(renderedMessage, true));
	}
}
