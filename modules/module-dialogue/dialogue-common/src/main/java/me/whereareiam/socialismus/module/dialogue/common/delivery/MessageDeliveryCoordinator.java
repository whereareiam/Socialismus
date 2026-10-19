package me.whereareiam.socialismus.module.dialogue.common.delivery;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.type.DeliveryStatus;
import me.whereareiam.socialismus.module.dialogue.common.DialogueNotices;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Delivers a private message on this server, through a remote delivery when its recipient is elsewhere, or tells the
 * sender the recipient is not online.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MessageDeliveryCoordinator {
	private final LocalDelivery localDelivery;
	private final RemoteDeliveries remoteDeliveries;
	private final DialogueNotices notices;
	private final PlayerRegistry playerRegistry;

	/**
	 * Returns whether a player may be reached: on this server, or elsewhere through a remote delivery.
	 *
	 * @param playerName the player's name, in any casing
	 */
	public boolean mayReach(@NotNull String playerName) {
		return playerRegistry.getPlayerData(playerName).isPresent()
				|| remoteDeliveries.current().map(remote -> remote.mayReach(playerName)).orElse(false);
	}

	/**
	 * Delivers a message written on this server: to its recipient here, through a remote delivery when they are
	 * elsewhere, or tells the sender why it did not reach them.
	 *
	 * @param message the message, whose status this sets
	 */
	public void processMessageDelivery(@NotNull Message message) {
		DeliveryStatus status = localDelivery.deliver(message, true);
		Optional<RemoteDelivery> remote = remoteDeliveries.current().filter(delivery -> delivery.mayReach(message.getRecipientName()));
		if (status == DeliveryStatus.RECIPIENT_OFFLINE && remote.isPresent()) {
			message.setDeliveryStatus(DeliveryStatus.PENDING_DELIVERY);
			remote.get().send(message);
			return;
		}

		message.setDeliveryStatus(status);
		notices.undelivered(message.getSender(), message.getRecipientName(), status);
	}
}
