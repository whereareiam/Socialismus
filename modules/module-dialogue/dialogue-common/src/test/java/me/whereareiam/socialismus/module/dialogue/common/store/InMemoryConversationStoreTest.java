package me.whereareiam.socialismus.module.dialogue.common.store;

import me.whereareiam.socialismus.module.dialogue.api.model.conversation.Conversation;
import me.whereareiam.socialismus.module.dialogue.api.model.conversation.ConversationKey;
import me.whereareiam.socialismus.module.dialogue.common.TestDataBuilder;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryConversationStoreTest {
	private final MutableClock clock = new MutableClock(Instant.parse("2026-01-01T10:00:00Z"));
	private final InMemoryConversationStore store = new InMemoryConversationStore(clock);

	@Test
	void findsAConversationWhateverTheCasingOfItsParticipants() {
		Conversation conversation = TestDataBuilder.conversationBetween("Alice", "Bob").build();

		store.save(conversation, Duration.ofHours(1));

		assertSame(conversation, store.find(ConversationKey.of("BOB", "alice")).orElseThrow());
		assertEquals(1, store.findByParticipant("ALICE").size());
	}

	@Test
	void forgetsAConversationOnceItsTimeIsUp() {
		store.save(TestDataBuilder.conversationBetween("Alice", "Bob").build(), Duration.ofHours(1));

		clock.instant = clock.instant.plus(Duration.ofHours(1));

		assertTrue(store.find(ConversationKey.of("Alice", "Bob")).isEmpty());
		assertTrue(store.findByParticipant("Alice").isEmpty());
	}

	@Test
	void deletesAConversation() {
		store.save(TestDataBuilder.conversationBetween("Alice", "Bob").build(), Duration.ofHours(1));

		store.delete(ConversationKey.of("Alice", "Bob"));

		assertTrue(store.find(ConversationKey.of("Alice", "Bob")).isEmpty());
	}

	private static final class MutableClock extends Clock {
		private Instant instant;

		private MutableClock(Instant instant) {
			this.instant = instant;
		}

		@Override
		public ZoneId getZone() {
			return ZoneOffset.UTC;
		}

		@Override
		public Clock withZone(ZoneId zone) {
			return this;
		}

		@Override
		public Instant instant() {
			return instant;
		}
	}
}
