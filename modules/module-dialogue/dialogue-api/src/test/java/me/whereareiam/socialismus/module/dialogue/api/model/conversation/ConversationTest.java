package me.whereareiam.socialismus.module.dialogue.api.model.conversation;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConversationTest {
	@Test
	void builtConversationTakesItsFirstMessage() {
		Conversation conversation = Conversation.builder()
				.id("conversation")
				.participants(Set.of("alice", "bob"))
				.build();

		conversation.addMessage(messageFrom("alice"));

		assertEquals(1, conversation.getMessages().size());
		assertEquals(1, conversation.getMessageCount());
		assertEquals("alice", conversation.getLastSender());
		assertEquals("conversation", conversation.getMessages().getFirst().getConversationId());
	}

	@Test
	void partnerIsTheOtherParticipantAsTheySpellThemselves() {
		Conversation conversation = Conversation.builder()
				.participants(Set.of("Alice", "Bob"))
				.build();

		assertEquals("Bob", conversation.partnerOf("alice"));
		assertEquals("Alice", conversation.partnerOf("BOB"));
	}

	private static Message messageFrom(String username) {
		SocialismusPlayer sender = mock(SocialismusPlayer.class);
		when(sender.getUsername()).thenReturn(username);

		return Message.builder()
				.sender(sender)
				.recipientName("bob")
				.content("hello")
				.timestamp(Instant.now())
				.build();
	}
}
