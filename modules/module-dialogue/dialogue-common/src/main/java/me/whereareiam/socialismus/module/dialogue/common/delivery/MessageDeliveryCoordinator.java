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

	public void processMessageDelivery(@NotNull Message message) {
		if (localDelivery.deliver(message, true)) {
			message.setDeliveryStatus(DeliveryStatus.DELIVERED_LOCALLY);
			return;
		}

		if (networkBridge.isEnabled()) {
			networkBridge.publish(message);
			return;
		}

		message.setDeliveryStatus(DeliveryStatus.RECIPIENT_OFFLINE);
		notices.noRecipient(message.getSender(), message.getRecipientName());
	}
}
