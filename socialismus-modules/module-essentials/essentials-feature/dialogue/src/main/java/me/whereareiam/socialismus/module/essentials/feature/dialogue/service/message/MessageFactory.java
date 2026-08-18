package me.whereareiam.socialismus.module.essentials.feature.dialogue.service.message;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.model.Dialogue;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.model.Message;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.type.DeliveryStatus;

import java.time.Instant;
import java.util.UUID;

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
				.conversationId(conversationId)
				.build();
	}

	public Dialogue createDialogue(SocialismusPlayer sender, String recipient, String content) {
		return Dialogue.builder()
				.sender(sender)
				.recipientName(recipient)
				.content(content)
				.build();
	}
}
