package me.whereareiam.socialismus.module.dialogue.common.conversation;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.api.model.conversation.Conversation;
import me.whereareiam.socialismus.module.dialogue.common.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConversationServiceTest {
	private final SocialismusPlayer alice = TestDataBuilder.mockPlayer("Alice");
	private final DialogueSettings settings = TestDataBuilder.defaultSettings();
	private final InMemoryConversationStore store = new InMemoryConversationStore();

	private ConversationService conversationService;

	@BeforeEach
	void setUp() {
		conversationService = new ConversationService(store, () -> settings);
	}

	@Test
	void startsAConversationWithAnEmptyMessageList() {
		Conversation conversation = conversationService.getOrCreateConversation("Alice", "Bob");

		assertNotNull(conversation.getId());
		assertEquals(2, conversation.getParticipants().size());
		assertTrue(conversation.getMessages().isEmpty());
		assertEquals(0, conversation.getMessageCount());
	}

	@Test
	void returnsTheConversationOnceAMessageWasAdded() {
		Conversation conversation = conversationService.getOrCreateConversation("Alice", "Bob");
		conversationService.addMessage(conversation, TestDataBuilder.messageFrom(alice, "Bob").build());

		assertSame(conversation, conversationService.getOrCreateConversation("bob", "alice"));
	}

	@Test
	void addsTheMessageAndNamesItsSender() {
		Conversation conversation = conversationService.getOrCreateConversation("Alice", "Bob");
		Message message = TestDataBuilder.messageFrom(alice, "Bob").build();

		conversationService.addMessage(conversation, message);

		assertEquals(List.of(message), conversation.getMessages());
		assertEquals("Alice", conversation.getLastSender());
		assertEquals(conversation.getId(), message.getConversationId());
	}

	@Test
	void dropsTheOldestMessagesBeyondTheLimit() {
		settings.getMessaging().getConversations().setMaxMessages(2);
		Conversation conversation = conversationService.getOrCreateConversation("Alice", "Bob");
		Message first = TestDataBuilder.messageFrom(alice, "Bob").build();
		Message second = TestDataBuilder.messageFrom(alice, "Bob").build();
		Message third = TestDataBuilder.messageFrom(alice, "Bob").build();

		List.of(first, second, third).forEach(message -> conversationService.addMessage(conversation, message));

		assertEquals(List.of(second, third), conversation.getMessages());
		assertEquals(3, conversation.getMessageCount());
	}

	@Test
	void listsAPlayersConversationsTheMostRecentFirst() {
		addMessageAt("Bob", Instant.parse("2026-01-01T10:00:00Z"));
		addMessageAt("Carol", Instant.parse("2026-01-01T11:00:00Z"));

		List<Conversation> conversations = conversationService.getPlayerConversations("alice");

		assertEquals(List.of("Carol", "Bob"), conversations.stream().map(c -> c.partnerOf("Alice")).toList());
	}

	@Test
	void dropsTheLeastRecentConversationsBeyondTheLimit() {
		settings.getMessaging().getConversations().setMaxConversations(1);
		addMessageAt("Bob", Instant.parse("2026-01-01T10:00:00Z"));
		addMessageAt("Carol", Instant.parse("2026-01-01T11:00:00Z"));

		List<Conversation> conversations = conversationService.getPlayerConversations("Alice");

		assertEquals(1, conversations.size());
		assertEquals("Carol", conversations.getFirst().partnerOf("Alice"));
	}

	private void addMessageAt(String partner, Instant timestamp) {
		Conversation conversation = conversationService.getOrCreateConversation("Alice", partner);
		conversationService.addMessage(conversation, TestDataBuilder.messageFrom(alice, partner).timestamp(timestamp).build());
	}
}
