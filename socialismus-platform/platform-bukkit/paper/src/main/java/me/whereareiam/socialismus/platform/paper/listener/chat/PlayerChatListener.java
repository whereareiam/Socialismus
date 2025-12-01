package me.whereareiam.socialismus.platform.paper.listener.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.input.sync.ChatSyncBus;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.output.listener.DynamicListener;
import me.whereareiam.socialismus.common.chat.ChatCoordinator;
import me.whereareiam.socialismus.common.chat.ChatMessageFactory;
import me.whereareiam.socialismus.common.chat.broadcast.ChatBroadcaster;
import me.whereareiam.socialismus.platform.paper.renderer.SocialismusRenderer;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PlayerChatListener implements DynamicListener<AsyncChatEvent> {
	private final ChatSyncBus chatSyncBus;
	private final ChatCoordinator chatCoordinator;
	private final ChatMessageFactory chatMessageFactory;
	private final ChatBroadcaster chatBroadcaster;

	@Override
	public void onEvent(AsyncChatEvent event) {
		Player sender = event.getPlayer();
		Set<Audience> nonPlayerAudiences = event.viewers().stream()
				.filter(aud -> !(aud instanceof Player))
				.collect(Collectors.toSet());

		Set<UUID> playerRecipientUuids = event.viewers().stream()
				.filter(aud -> aud instanceof Player)
				.map(aud -> ((Player) aud).getUniqueId())
				.collect(Collectors.toSet());

		ChatMessage chatMessage = chatMessageFactory.createChatMessage(
				sender.getUniqueId(),
				playerRecipientUuids,
				event.message()
		);

		FormattedChatMessage formatted = chatCoordinator.coordinate(chatMessage);
		if (formatted == null
				|| formatted.isCancelled()
				|| !formatted.isVanillaSending()) {
			event.setCancelled(true);
			return;
		}

		event.viewers().clear();

		Set<Audience> newPlayerViewers = formatted.getRecipients().stream()
				.map(rec -> sender.getServer().getPlayer(rec.getUniqueId()))
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
		event.viewers().addAll(newPlayerViewers);

		event.viewers().addAll(nonPlayerAudiences);

		event.renderer(new SocialismusRenderer(formatted, chatBroadcaster));
	}

}
