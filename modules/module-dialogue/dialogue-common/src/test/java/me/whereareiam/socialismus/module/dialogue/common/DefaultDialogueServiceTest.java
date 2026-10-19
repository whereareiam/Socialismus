package me.whereareiam.socialismus.module.dialogue.common;

import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.RequirementReason;
import me.whereareiam.socialismus.module.dialogue.api.event.DialogueSendEvent;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.api.model.conversation.Conversation;
import me.whereareiam.socialismus.module.dialogue.common.conversation.ConversationModes;
import me.whereareiam.socialismus.module.dialogue.common.conversation.ConversationService;
import me.whereareiam.socialismus.module.dialogue.common.delivery.MessageDeliveryCoordinator;
import me.whereareiam.socialismus.module.dialogue.common.delivery.MessageFactory;
import me.whereareiam.socialismus.module.dialogue.common.store.InMemoryConversationStore;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DefaultDialogueServiceTest {
	private final MessagingRequirements requirements = mock(MessagingRequirements.class);
	private final MessageDeliveryCoordinator deliveryCoordinator = mock(MessageDeliveryCoordinator.class);
	private final DialogueNotices notices = mock(DialogueNotices.class);
	private final PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
	private final EventManager eventManager = mock(EventManager.class);
	private final ConversationModes conversationModes = new ConversationModes();
	private final DialogueSettings settings = TestDataBuilder.defaultSettings();
	private final ConversationService conversationService =
			new ConversationService(new InMemoryConversationStore(), () -> settings);

	private final SocialismusPlayer alice = TestDataBuilder.mockPlayer("Alice");
	private final SocialismusPlayer bob = TestDataBuilder.mockPlayer("Bob");

	private DefaultDialogueService dialogueService;

	@BeforeEach
	void setUp() {
		when(playerRegistry.getPlayerData(anyString())).thenReturn(Optional.empty());
		when(requirements.judge(any(), any(), any())).thenReturn(MessagingRequirements.Verdict.ALLOWED);

		dialogueService = new DefaultDialogueService(
				conversationService,
				new MessageFactory(),
				requirements,
				deliveryCoordinator,
				notices,
				playerRegistry,
				() -> settings,
				eventManager,
				conversationModes,
				mock(SenderPrivileges.class)
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
	void anAddOnStopsAMessageByCancellingTheSendEvent() {
		online(bob);
		doAnswer(call -> {
			call.getArgument(0, DialogueSendEvent.class).setCancelled(true);
			return null;
		}).when(eventManager).call(any(DialogueSendEvent.class));

		dialogueService.send(alice, "bob", "hello");

		verifyNoInteractions(deliveryCoordinator);
		assertTrue(conversationService.getPlayerConversations("Alice").isEmpty());
	}

	@Test
	void anAddOnChangesWhatIsSent() {
		online(bob);
		doAnswer(call -> {
			call.getArgument(0, DialogueSendEvent.class).setContent("h*llo");
			return null;
		}).when(eventManager).call(any(DialogueSendEvent.class));

		dialogueService.send(alice, "bob", "hello");

		assertEquals("h*llo", delivered().getContent());
	}

	@Test
	void theSendEventNamesTheRecipientAsTheySpellThemselves() {
		online(bob);

		dialogueService.send(alice, "bob", "hello");

		ArgumentCaptor<DialogueSendEvent> event = ArgumentCaptor.forClass(DialogueSendEvent.class);
		verify(eventManager).call(event.capture());
		assertEquals("Bob", event.getValue().getRecipientName());
		assertEquals(bob, event.getValue().getRecipient());
	}

	@Test
	void namingAPartnerWithoutAMessageStartsAConversationAndNamingThemAgainEndsIt() {
		online(bob);

		dialogueService.converse(alice, "bob");
		assertEquals(Optional.of("Bob"), dialogueService.conversationPartner(alice));
		verify(notices).conversationEntered(alice, "Bob");

		dialogueService.converse(alice, "BOB");
		assertEquals(Optional.empty(), dialogueService.conversationPartner(alice));
		verify(notices).conversationLeft(alice, "Bob");
	}

	@Test
	void namingAnotherPartnerMovesTheConversation() {
		SocialismusPlayer carol = TestDataBuilder.mockPlayer("Carol");
		online(bob);
		online(carol);

		dialogueService.converse(alice, "bob");
		dialogueService.converse(alice, "carol");

		assertEquals(Optional.of("Carol"), dialogueService.conversationPartner(alice));
	}

	@Test
	void conversationModeCanBeTurnedOff() {
		settings.getMessaging().setConversationMode(false);

		dialogueService.converse(alice, "bob");

		verify(notices).noConversationMode(alice);
		assertEquals(Optional.empty(), dialogueService.conversationPartner(alice));
	}

	@Test
	void noConversationWithOneself() {
		online(alice);

		dialogueService.converse(alice, "alice");

		verify(notices).samePlayer(alice);
		assertEquals(Optional.empty(), dialogueService.conversationPartner(alice));
	}

	@Test
	void aRecipientHiddenFromTheSenderLooksOffline() {
		online(bob);
		when(alice.canSee(bob)).thenReturn(false);

		dialogueService.send(alice, "bob", "hello");

		verify(notices).noRecipient(alice, "bob");
		verifyNoInteractions(deliveryCoordinator);
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
		RequirementReason reason = RequirementReason.of("Bob is elsewhere", Map.of());
		when(requirements.judge(alice, bob, "Bob")).thenReturn(new MessagingRequirements.Verdict(false, reason));

		dialogueService.send(alice, "bob", "hello");

		verify(notices).notAllowed(alice, "Bob", reason);
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
