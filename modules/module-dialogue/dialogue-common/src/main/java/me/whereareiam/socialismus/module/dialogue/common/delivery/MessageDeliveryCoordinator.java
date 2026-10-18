package me.whereareiam.socialismus.module.dialogue.common.delivery;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.type.DeliveryStatus;
import me.whereareiam.socialismus.module.dialogue.common.DialogueNotices;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * Delivers a private message on this server, through the network when its recipient is elsewhere, or tells the
 * sender the recipient is not online.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class MessageDeliveryCoordinator {
	private final LocalDelivery localDelivery;
	private final DialogueNetworkBridge networkBridge;
	private final DialogueNotices notices;
	private final PlayerRegistry playerRegistry;

	/**
	 * Returns whether a player may be online where a message reaches them: on this server, or anywhere when
	 * messages go to the other servers, which only a delivery confirmation tells.
	 *
	 * @param playerName the player's name, in any casing
	 */
	public boolean mayReach(@NotNull String playerName) {
		return playerRegistry.getPlayerData(playerName).isPresent() || networkBridge.isEnabled();
	}

	/**
	 * Delivers a message written on this server: to its recipient here, through the network when they are not here
	 * and the network is on, or tells the sender why it did not reach them.
	 *
	 * @param message the message, whose status this sets
	 */
	public void processMessageDelivery(@NotNull Message message) {
		DeliveryStatus status = localDelivery.deliver(message, true);
		if (status == DeliveryStatus.RECIPIENT_OFFLINE && networkBridge.isEnabled()) {
			message.setDeliveryStatus(DeliveryStatus.PENDING_DELIVERY);
			networkBridge.publish(message);
			return;
		}

		message.setDeliveryStatus(status);
		notices.undelivered(message.getSender(), message.getRecipientName(), status);
	}
}
