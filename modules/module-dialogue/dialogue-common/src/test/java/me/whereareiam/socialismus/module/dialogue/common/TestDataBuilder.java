package me.whereareiam.socialismus.module.dialogue.common;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.api.model.conversation.Conversation;
import me.whereareiam.socialismus.module.dialogue.api.type.DeliveryStatus;
import me.whereareiam.socialismus.module.dialogue.common.config.defaults.DialogueSettingsDefaults;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test data builder utility for creating test objects
 */
public class TestDataBuilder {
	public static Conversation.ConversationBuilder<?, ?> conversationBetween(String player1, String player2) {
		return Conversation.builder()
				.id(UUID.randomUUID().toString())
				.participants(Set.of(player1, player2))
				.messages(new ArrayList<>())
				.messageCount(0)
				.lastActivity(Instant.now());
	}

	public static Message.MessageBuilder<?, ?> messageFrom(SocialismusPlayer sender, String recipientName) {
		return Message.builder()
				.id(UUID.randomUUID().toString())
				.sender(sender)
				.recipientName(recipientName)
				.timestamp(Instant.now())
				.deliveryStatus(DeliveryStatus.PENDING_DELIVERY)
				.content("Test message");
	}

	public static SocialismusPlayer mockPlayer(String username) {
		SocialismusPlayer player = mock(SocialismusPlayer.class);
		when(player.getUsername()).thenReturn(username);
		return player;
	}

	public static DialogueSettings defaultSettings() {
		return new DialogueSettingsDefaults().supply(new DialogueSettings());
	}
}
