package me.whereareiam.socialismus.module.essentials.feature.dialogue.service;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.TestDataBuilder;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.model.conversation.ConversationThread;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.conversation.ConversationService;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.dialogue.DialogueErrorHandler;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.dialogue.DialogueManager;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.message.MessageDeliveryCoordinator;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.message.MessageFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests DialogueManager business logic without serialization dependencies
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DialogueManagerTest {

	@Mock
	private ConversationService conversationService;
	@Mock
	private MessageFactory messageFactory;
	@Mock
	private ReplyValidator validator;
	@Mock
	private DialogueErrorHandler errorHandler;
	@Mock
	private MessageDeliveryCoordinator deliveryCoordinator;

	private DialogueManager dialogueManager;

	@BeforeEach
	void setUp() {
		dialogueManager = new DialogueManager(
				conversationService,
				messageFactory,
				validator,
				errorHandler,
				deliveryCoordinator
		);
	}

	@Test
	void shouldProcessValidMessageSuccessfully() {
		// Given
		SocialismusPlayer sender = TestDataBuilder.mockPlayer("alice");
		String recipient = "bob";
		String message = "Hello Bob!";

		ConversationThread thread = TestDataBuilder.conversationThreadBetween("alice", "bob").build();
		when(conversationService.getOrCreateConversation("alice", "bob")).thenReturn(thread);
		when(validator.isSelfMessage(sender, recipient)).thenReturn(false);
		when(messageFactory.createMessage(eq(sender), eq(recipient), eq(message), anyString()))
				.thenReturn(TestDataBuilder.messageFrom(sender, recipient).build());
		when(messageFactory.createDialogue(sender, recipient, message))
				.thenReturn(TestDataBuilder.createDialogue(sender, recipient, message));

		// When
		dialogueManager.send(sender, recipient, message);

		// Then - Verify the core business logic was executed
		verify(conversationService).getOrCreateConversation("alice", "bob");
		verify(conversationService).addMessage(eq(thread), any());
		verify(deliveryCoordinator).processMessageDelivery(any(), any());
		verify(validator).isSelfMessage(sender, recipient);
	}

	@Test
	void shouldProcessReplyWithValidLastPartner() {
		// Given
		SocialismusPlayer sender = TestDataBuilder.mockPlayer("alice");
		String message = "Reply message";

		ConversationThread thread = TestDataBuilder.conversationThreadBetween("alice", "bob").build();
		when(validator.isReplyEnabled()).thenReturn(true);
		when(conversationService.getLastConversationPartner("alice"))
				.thenReturn(Optional.of("bob"));
		when(conversationService.getOrCreateConversation("alice", "bob")).thenReturn(thread);
		when(validator.isSelfMessage(sender, "bob")).thenReturn(false);
		when(messageFactory.createMessage(eq(sender), eq("bob"), eq(message), anyString()))
				.thenReturn(TestDataBuilder.messageFrom(sender, "bob").build());
		when(messageFactory.createDialogue(sender, "bob", message))
				.thenReturn(TestDataBuilder.createDialogue(sender, "bob", message));

		// When
		dialogueManager.sendReply(sender, message);

		// Then - Verify reply logic works
		verify(validator).isReplyEnabled();
		verify(conversationService).getLastConversationPartner("alice");
		verify(conversationService).getOrCreateConversation("alice", "bob");
		verify(deliveryCoordinator).processMessageDelivery(any(), any());
	}

	@Test
	void shouldValidateReplyToSpecificPlayer() {
		// Given
		SocialismusPlayer sender = TestDataBuilder.mockPlayer("alice");
		String recipient = "bob";
		String message = "Reply to Bob";

		ConversationThread thread = TestDataBuilder.conversationThreadBetween("alice", "bob").build();
		when(validator.isReplyEnabled()).thenReturn(true);
		when(validator.conversationExists("alice", "bob")).thenReturn(true);
		when(conversationService.getOrCreateConversation("alice", "bob")).thenReturn(thread);
		when(validator.isSelfMessage(sender, recipient)).thenReturn(false);
		when(messageFactory.createMessage(eq(sender), eq(recipient), eq(message), anyString()))
				.thenReturn(TestDataBuilder.messageFrom(sender, recipient).build());
		when(messageFactory.createDialogue(sender, recipient, message))
				.thenReturn(TestDataBuilder.createDialogue(sender, recipient, message));

		// When
		dialogueManager.sendReplyTo(sender, recipient, message);

		// Then - Verify conversation validation and message sending
		verify(validator).isReplyEnabled();
		verify(validator).conversationExists("alice", "bob");
		verify(conversationService).getOrCreateConversation("alice", "bob");
		verify(deliveryCoordinator).processMessageDelivery(any(), any());
	}

}
