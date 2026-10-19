package me.whereareiam.socialismus.module.dialogue.network.store;

import me.whereareiam.socialismus.module.dialogue.api.model.conversation.Conversation;
import me.whereareiam.socialismus.module.dialogue.api.model.conversation.ConversationKey;
import me.whereareiam.socialismus.module.dialogue.network.TestNodes;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReplicatedConversationStoreTest {
	private final TestNodes nodes = new TestNodes();

	@Test
	void aConversationHeldOnOneNodeIsFoundOnAnother() {
		ReplicatedConversationStore lobby = new ReplicatedConversationStore(nodes.node("lobby").replication());
		ReplicatedConversationStore survival = new ReplicatedConversationStore(nodes.node("survival").replication());

		lobby.save(conversation("Alice", "Bob", 10), Duration.ofHours(1));

		Conversation found = survival.find(ConversationKey.of("alice", "BOB")).orElseThrow();
		assertEquals("Bob", found.partnerOf("Alice"));
		assertEquals("Alice", found.getLastSender());
		assertEquals(3, found.getMessageCount());
	}

	@Test
	void aPlayersConversationsComeMostRecentFirst() {
		ReplicatedConversationStore lobby = new ReplicatedConversationStore(nodes.node("lobby").replication());
		ReplicatedConversationStore survival = new ReplicatedConversationStore(nodes.node("survival").replication());
		lobby.save(conversation("Alice", "Bob", 10), Duration.ofHours(1));
		lobby.save(conversation("Alice", "Carol", 20), Duration.ofHours(1));

		List<Conversation> found = survival.findByParticipant("alice");

		assertEquals(List.of("Carol", "Bob"), found.stream().map(conversation -> conversation.partnerOf("Alice")).toList());
	}

	@Test
	void aConversationExpiresOnEveryNode() {
		ReplicatedConversationStore lobby = new ReplicatedConversationStore(nodes.node("lobby").replication());
		ReplicatedConversationStore survival = new ReplicatedConversationStore(nodes.node("survival").replication());
		lobby.save(conversation("Alice", "Bob", 10), Duration.ofMinutes(5));

		nodes.clock.advance(Duration.ofMinutes(6));

		assertTrue(survival.find(ConversationKey.of("Alice", "Bob")).isEmpty());
	}

	private static Conversation conversation(String sender, String partner, long secondsAgoFrom) {
		return Conversation.builder()
				.id(sender + "-" + partner)
				.participants(Set.of(sender, partner))
				.lastSender(sender)
				.lastActivity(Instant.ofEpochSecond(1_000_000 + secondsAgoFrom))
				.messageCount(3)
				.build();
	}
}
