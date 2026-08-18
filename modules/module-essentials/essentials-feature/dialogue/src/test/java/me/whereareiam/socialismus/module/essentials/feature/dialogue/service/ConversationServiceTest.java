package me.whereareiam.socialismus.module.essentials.feature.dialogue.service;

import com.google.inject.Provider;
import me.whereareiam.socialismus.service.resource.CacheService;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.TestDataBuilder;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.config.DialogueSettings;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.model.Message;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.model.conversation.ConversationThread;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.conversation.ConversationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ConversationServiceTest {

	@Mock
	private CacheService cacheService;

	@Mock
	private Provider<DialogueSettings> settingsProvider;
	private ConversationService conversationService;

	@BeforeEach
	void setUp() {
		conversationService = new ConversationService(cacheService, settingsProvider);
	}

	@Test
	void shouldCreateNewConversationWhenNotExists() {
		// Given
		String player1 = "alice";
		String player2 = "bob";
		when(cacheService.get(anyString(), eq(ConversationThread.class)))
				.thenReturn(Optional.empty());

		DialogueSettings settings = new DialogueSettings();
		DialogueSettings.MessageHistory history = new DialogueSettings.MessageHistory();
		history.setTtl("PT24H");
		history.setEnabled(true);
		history.setMaxConversations(10);
		history.setMaxMessagesPerConversation(50);
		settings.setMessageHistory(history);
		when(settingsProvider.get()).thenReturn(settings);

		// When
		ConversationThread result = conversationService.getOrCreateConversation(player1, player2);

		// Then
		assertNotNull(result);
		assertTrue(result.getParticipants().contains(player1));
		assertTrue(result.getParticipants().contains(player2));
		assertEquals(2, result.getParticipants().size());
		assertTrue(result.isActive());
		assertEquals(0, result.getMessageCount());
		assertNotNull(result.getId());

		// Verify conversation was cached
		verify(cacheService).put(
				eq("pm:conversation:alice:bob"),
				eq(result),
				eq(Duration.parse("PT24H"))
		);
	}

	@Test
	void shouldReturnExistingConversationWhenExists() {
		// Given
		String player1 = "alice";
		String player2 = "bob";
		ConversationThread existingThread = TestDataBuilder.conversationThreadBetween(player1, player2)
				.messageCount(5)
				.build();

		when(cacheService.get("pm:conversation:alice:bob", ConversationThread.class))
				.thenReturn(Optional.of(existingThread));

		// When
		ConversationThread result = conversationService.getOrCreateConversation(player1, player2);

		// Then
		assertSame(existingThread, result);
		assertEquals(5, result.getMessageCount());

		// Verify no new conversation was created
		verify(cacheService, never()).put(anyString(), any(ConversationThread.class), any(Duration.class));
	}

	@Test
	void shouldAddMessageToConversationAndUpdateCache() {
		// Given
		ConversationThread thread = TestDataBuilder.conversationThread()
				.participants(Set.of("alice", "bob"))
				.messageCount(2)
				.build();

		Message message = TestDataBuilder.messageFrom(
				TestDataBuilder.mockPlayer("alice"), "bob"
		).build();

		DialogueSettings settings = new DialogueSettings();
		DialogueSettings.MessageHistory history = new DialogueSettings.MessageHistory();
		history.setTtl("PT24H");
		history.setEnabled(true);
		history.setMaxConversations(10);
		history.setMaxMessagesPerConversation(50);
		settings.setMessageHistory(history);
		when(settingsProvider.get()).thenReturn(settings);

		// When
		conversationService.addMessage(thread, message);

		// Then
		assertTrue(thread.getMessages().contains(message));
		assertEquals("alice", thread.getLastSender());
		assertEquals(3, thread.getMessageCount());
		assertEquals(thread.getId(), message.getConversationId());

		// Verify conversation was updated in cache
		verify(cacheService).put(
				eq("pm:conversation:alice:bob"),
				eq(thread),
				any(Duration.class)
		);
	}

	@Test
	void shouldGetPlayerConversations() {
		// Given
		String playerName = "alice";
		List<String> conversationKeys = List.of("alice:bob", "alice:charlie");
		when(cacheService.get("pm:player_conversations:alice", List.class))
				.thenReturn(Optional.of(conversationKeys));

		DialogueSettings settings = new DialogueSettings();
		DialogueSettings.MessageHistory history = new DialogueSettings.MessageHistory();
		history.setMaxConversations(10);
		settings.setMessageHistory(history);
		when(settingsProvider.get()).thenReturn(settings);

		// When
		List<String> result = conversationService.getPlayerConversations(playerName);

		// Then
		assertEquals(2, result.size());
		assertTrue(result.contains("alice:bob"));
		assertTrue(result.contains("alice:charlie"));
	}

	@Test
	void shouldReturnLastConversationPartner() {
		// Given
		String playerName = "alice";
		// The last conversation in the list should be the most recent
		List<String> conversationKeys = List.of("alice:bob", "alice:charlie");
		when(cacheService.get("pm:player_conversations:alice", List.class))
				.thenReturn(Optional.of(conversationKeys));

		DialogueSettings settings = new DialogueSettings();
		DialogueSettings.MessageHistory history = new DialogueSettings.MessageHistory();
		history.setMaxConversations(10);
		settings.setMessageHistory(history);
		when(settingsProvider.get()).thenReturn(settings);

		// When
		Optional<String> result = conversationService.getLastConversationPartner(playerName);

		// Then
		// Should return the last conversation partner (charlie - at end of list)
		assertTrue(result.isPresent());
		assertEquals("charlie", result.get());
	}

	@Test
	void shouldReturnEmptyWhenNoConversations() {
		// Given
		String playerName = "alice";
		when(cacheService.get("pm:player_conversations:alice", List.class))
				.thenReturn(Optional.empty());

		DialogueSettings settings = new DialogueSettings();
		DialogueSettings.MessageHistory history = new DialogueSettings.MessageHistory();
		history.setMaxConversations(10);
		settings.setMessageHistory(history);
		when(settingsProvider.get()).thenReturn(settings);

		// When
		Optional<String> result = conversationService.getLastConversationPartner(playerName);

		// Then
		assertTrue(result.isEmpty());
	}

	@Test
	void shouldDeactivateConversation() {
		// Given
		String player1 = "alice";
		String player2 = "bob";
		ConversationThread activeThread = TestDataBuilder.conversationThreadBetween(player1, player2)
				.active(true)
				.build();

		when(cacheService.get("pm:conversation:alice:bob", ConversationThread.class))
				.thenReturn(Optional.of(activeThread));

		DialogueSettings settings = new DialogueSettings();
		DialogueSettings.MessageHistory history = new DialogueSettings.MessageHistory();
		history.setTtl("PT24H");
		settings.setMessageHistory(history);
		when(settingsProvider.get()).thenReturn(settings);

		// When
		conversationService.deactivateConversation(player1, player2);

		// Then
		assertFalse(activeThread.isActive());
		verify(cacheService).put(
				eq("pm:conversation:alice:bob"),
				eq(activeThread),
				eq(Duration.parse("PT24H"))
		);
	}
}
