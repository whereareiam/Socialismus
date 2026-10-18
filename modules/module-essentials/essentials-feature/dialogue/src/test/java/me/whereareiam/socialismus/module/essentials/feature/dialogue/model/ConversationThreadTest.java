package me.whereareiam.socialismus.module.essentials.feature.dialogue.model;

import me.whereareiam.socialismus.module.essentials.feature.dialogue.TestDataBuilder;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.model.conversation.ConversationThread;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConversationThreadTest {
	@Test
	void builtThreadTakesItsFirstMessage() {
		ConversationThread thread = ConversationThread.builder()
				.id("thread")
				.participants(Set.of("alice", "bob"))
				.build();

		thread.addMessage(TestDataBuilder.messageFrom(TestDataBuilder.mockPlayer("alice"), "bob").build());

		assertEquals(1, thread.getMessages().size());
		assertEquals(1, thread.getMessageCount());
	}
}
