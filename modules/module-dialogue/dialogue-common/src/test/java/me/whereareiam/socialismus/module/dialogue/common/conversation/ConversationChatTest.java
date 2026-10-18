package me.whereareiam.socialismus.module.dialogue.common.conversation;

import me.whereareiam.socialismus.event.chat.ChatReceivedEvent;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.DialogueService;
import me.whereareiam.socialismus.module.dialogue.common.TestDataBuilder;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ConversationChatTest {
	private final DialogueService dialogueService = mock(DialogueService.class);
	private final ConversationChat conversationChat = new ConversationChat(dialogueService);
	private final SocialismusPlayer alice = TestDataBuilder.mockPlayer("Alice");

	@Test
	void sendsTheChatOfAPlayerInConversationModeToTheirPartner() {
		when(dialogueService.conversationPartner(alice)).thenReturn(Optional.of("Bob"));
		ChatReceivedEvent event = new ChatReceivedEvent(ChatMessage.builder().sender(alice).content(Component.text("hi there")).build());

		conversationChat.onChat(event);

		assertTrue(event.isCancelled());
		verify(dialogueService).send(alice, "Bob", "hi there");
	}

	@Test
	void leavesTheChatOfOtherPlayersInChat() {
		when(dialogueService.conversationPartner(alice)).thenReturn(Optional.empty());
		ChatReceivedEvent event = new ChatReceivedEvent(ChatMessage.builder().sender(alice).content(Component.text("hi")).build());

		conversationChat.onChat(event);

		assertFalse(event.isCancelled());
		verify(dialogueService, never()).send(any(), anyString(), anyString());
	}
}
