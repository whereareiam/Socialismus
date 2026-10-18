package me.whereareiam.socialismus.module.dialogue.common.delivery;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.event.DialogueDeliveredEvent;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Delivers private messages to the players of this server.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class LocalDelivery {
	private final PlayerRegistry playerRegistry;
	private final MessageRenderer renderer;
	private final EventManager eventManager;

	/**
	 * Shows a message to its recipient when they are on this server, and to its sender when they wrote it here.
	 *
	 * @param message    the message
	 * @param showSender whether the sender is on this server and sees their message
	 * @return whether the recipient is on this server
	 */
	public boolean deliver(@NotNull Message message, boolean showSender) {
		Optional<SocialismusPlayer> recipient = playerRegistry.getPlayerData(message.getRecipientName());
		if (recipient.isEmpty()) return false;

		if (showSender) renderer.showSent(message, recipient.get());
		renderer.showReceived(message, recipient.get());
		eventManager.call(new DialogueDeliveredEvent(message, recipient.get()));

		return true;
	}
}
