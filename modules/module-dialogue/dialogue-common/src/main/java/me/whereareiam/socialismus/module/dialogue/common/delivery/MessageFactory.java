package me.whereareiam.socialismus.module.dialogue.common.delivery;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.type.DeliveryStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Creates the private messages this server sends.
 */
@Singleton
public class MessageFactory {
	public Message createMessage(SocialismusPlayer sender, String recipient, String content, String conversationId) {
		return Message.builder()
				.id(UUID.randomUUID().toString())
				.sender(sender)
				.recipientName(recipient)
				.content(content)
				.timestamp(Instant.now())
				.deliveryStatus(DeliveryStatus.PENDING_DELIVERY)
				.origin(Constants.Synchronization.IDENTIFIER)
				.conversationId(conversationId)
				.build();
	}
}
