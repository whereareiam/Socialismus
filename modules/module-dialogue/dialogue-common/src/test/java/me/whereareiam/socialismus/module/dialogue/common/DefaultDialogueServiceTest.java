package me.whereareiam.socialismus.module.dialogue.common;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.api.model.conversation.Conversation;
import me.whereareiam.socialismus.module.dialogue.common.conversation.ConversationService;
import me.whereareiam.socialismus.module.dialogue.common.conversation.InMemoryConversationStore;
import me.whereareiam.socialismus.module.dialogue.common.delivery.MessageDeliveryCoordinator;
import me.whereareiam.socialismus.module.dialogue.common.delivery.MessageFactory;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DefaultDialogueServiceTest {
	private final MessagingRequirements requirements = mock(MessagingRequirements.class);
	private final MessageDeliveryCoordinator deliveryCoordinator = mock(MessageDeliveryCoordinator.class);
	private final DialogueNotices notices = mock(DialogueNotices.class);
	private final PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
	private final DialogueSettings settings = TestDataBuilder.defaultSettings();
	private final ConversationService conversationService =
			new ConversationService(new InMemoryConversationStore(), () -> settings);

	private final SocialismusPlayer alice = TestDataBuilder.mockPlayer("Alice");
	private final SocialismusPlayer bob = TestDataBuilder.mockPlayer("Bob");

	private DefaultDialogueService dialogueService;

	@BeforeEach
	void setUp() {
		when(playerRegistry.getPlayerData(anyString())).thenReturn(Optional.empty());
		when(requirements.allows(any(), any())).thenReturn(true);

		dialogueService = new DefaultDialogueService(
				conversationService,
				new MessageFactory(),
				requirements,
				deliveryCoordinator,
				notices,
				playerRegistry,
				() -> settings
		);
	}

	@Test
	void sendsAMessageAndKeepsItInTheConversation() {
		online(bob);

		dialogueService.send(alice, "bob", "hello");

		Message message = delivered();
		assertEquals("Bob", message.getRecipientName());
		assertEquals("hello", message.getContent());
		assertEquals(List.of(message), conversationService.getOrCreateConversation("Alice", "Bob").getMessages());
	}

	@Test
	void refusesAMessageToOneself() {
		online(alice);

		dialogueService.send(alice, "alice", "hello");

		verify(notices).samePlayer(alice);
		verifyNoInteractions(deliveryCoordinator);
	}

	@Test
	void refusesAMessageTheRequirementsDoNotAllow() {
		online(bob);
		when(requirements.allows(alice, bob)).thenReturn(false);

		dialogueService.send(alice, "bob", "hello");

		verify(notices).notAllowed(alice, "Bob");
		verifyNoInteractions(deliveryCoordinator);
	}

	@Test
	void replyNamesThePartnerAsTheySpellThemselves() {
		SocialismusPlayer carol = TestDataBuilder.mockPlayer("Carol");
		Conversation conversation = conversationService.getOrCreateConversation("Bob", "carol");
		conversationService.addMessage(conversation, TestDataBuilder.messageFrom(bob, "carol").build());
		online(carol);
		when(deliveryCoordinator.mayReach("carol")).thenReturn(true);

		dialogueService.reply(bob, "thanks");

		assertEquals("Carol", delivered().getRecipientName());
	}

	@Test
	void replyAnswersTheMostRecentConversation() {
		SocialismusPlayer carol = TestDataBuilder.mockPlayer("Carol");
		online(carol);
		when(deliveryCoordinator.mayReach(anyString())).thenReturn(true);
		addMessage(alice, "Bob", Instant.now().minusSeconds(2));
		addMessage(carol, "Bob", Instant.now().minusSeconds(1));

		dialogueService.reply(bob, "thanks");

		assertEquals("Carol", delivered().getRecipientName());
	}

	@Test
	void replyWithoutConversationsSaysSo() {
		dialogueService.reply(alice, "hello");

		verify(notices).noConversations(alice);
		verifyNoInteractions(deliveryCoordinator);
	}

	@Test
	void replyToAConversationOlderThanTheTimeoutListsTheConversations() {
		Conversation conversation = conversationService.getOrCreateConversation("Alice", "Bob");
		Instant longAgo = Instant.now().minus(settings.getMessaging().getReply().getTimeout()).minus(Duration.ofMinutes(1));
		conversationService.addMessage(conversation, TestDataBuilder.messageFrom(alice, "Bob").timestamp(longAgo).build());

		dialogueService.reply(alice, "hello");

		verify(notices).noPartner(alice, List.of(conversation));
		verifyNoInteractions(deliveryCoordinator);
	}

	@Test
	void replyToAnOfflinePartnerIsRefusedUnlessAllowed() {
		Conversation conversation = conversationService.getOrCreateConversation("Alice", "Bob");
		conversationService.addMessage(conversation, TestDataBuilder.messageFrom(alice, "Bob").build());

		dialogueService.reply(alice, "hello");
		verify(notices).partnerOffline(alice, "Bob");
		verify(deliveryCoordinator, never()).processMessageDelivery(any());

		settings.getMessaging().getReply().setToOffline(true);
		dialogueService.reply(alice, "hello");
		assertEquals("Bob", delivered().getRecipientName());
	}

	private void addMessage(SocialismusPlayer sender, String recipient, Instant timestamp) {
		Conversation conversation = conversationService.getOrCreateConversation(sender.getUsername(), recipient);
		conversationService.addMessage(conversation, TestDataBuilder.messageFrom(sender, recipient).timestamp(timestamp).build());
	}

	private void online(SocialismusPlayer player) {
		when(playerRegistry.getPlayerData(player.getUsername().toLowerCase())).thenReturn(Optional.of(player));
		when(playerRegistry.getPlayerData(player.getUsername())).thenReturn(Optional.of(player));
	}

	private Message delivered() {
		ArgumentCaptor<Message> message = ArgumentCaptor.forClass(Message.class);
		verify(deliveryCoordinator, atLeastOnce()).processMessageDelivery(message.capture());

		return message.getValue();
	}
}
