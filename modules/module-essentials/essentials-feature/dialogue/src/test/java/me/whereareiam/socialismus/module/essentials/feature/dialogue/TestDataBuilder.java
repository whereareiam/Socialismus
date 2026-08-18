package me.whereareiam.socialismus.module.essentials.feature.dialogue;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.model.Dialogue;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.model.Message;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.model.conversation.ConversationThread;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.type.DeliveryStatus;

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
	public static ConversationThread.ConversationThreadBuilder conversationThread() {
		return ConversationThread.builder()
				.id(UUID.randomUUID().toString())
				.participants(Set.of("alice", "bob"))
				.messages(new ArrayList<>())
				.active(true)
				.messageCount(0)
				.lastActivity(Instant.now());
	}

	public static ConversationThread.ConversationThreadBuilder conversationThreadBetween(String player1, String player2) {
		return ConversationThread.builder()
				.id(UUID.randomUUID().toString())
				.participants(Set.of(player1, player2))
				.messages(new ArrayList<>())
				.active(true)
				.messageCount(0)
				.lastActivity(Instant.now());
	}

	public static Message.MessageBuilder messageFrom(SocialismusPlayer sender, String recipientName) {
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

	public static Dialogue createDialogue(SocialismusPlayer sender, String recipientName, String content) {
		return Dialogue.builder()
				.sender(sender)
				.recipientName(recipientName)
				.content(content)
				.build();
	}
}
