package me.whereareiam.socialismus.module.dialogue.network.store;

import me.whereareiam.socialismus.module.dialogue.api.model.IgnoredPlayer;
import me.whereareiam.socialismus.module.dialogue.network.TestNodes;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReplicatedIgnoreStoreTest {
	private static final UUID ALICE = UUID.randomUUID();
	private static final IgnoredPlayer BOB = new IgnoredPlayer(UUID.randomUUID(), "Bob");
	private static final IgnoredPlayer CAROL = new IgnoredPlayer(UUID.randomUUID(), "Carol");

	private final TestNodes nodes = new TestNodes();

	@Test
	void whomAPlayerIgnoresHoldsOnEveryNodeInOrder() {
		ReplicatedIgnoreStore lobby = new ReplicatedIgnoreStore(nodes.node("lobby").replication());
		ReplicatedIgnoreStore survival = new ReplicatedIgnoreStore(nodes.node("survival").replication());

		assertTrue(lobby.ignore(ALICE, BOB));
		assertTrue(survival.ignore(ALICE, CAROL));
		assertFalse(lobby.ignore(ALICE, BOB));

		assertEquals(List.of(BOB, CAROL), lobby.ignored(ALICE));
		assertTrue(survival.ignores(ALICE, BOB.getUniqueId()));
	}

	@Test
	void unignoringOnAnotherNodeTakesEffectEverywhere() {
		ReplicatedIgnoreStore lobby = new ReplicatedIgnoreStore(nodes.node("lobby").replication());
		ReplicatedIgnoreStore survival = new ReplicatedIgnoreStore(nodes.node("survival").replication());
		lobby.ignore(ALICE, BOB);

		assertTrue(survival.unignore(ALICE, BOB.getUniqueId()));
		assertFalse(survival.unignore(ALICE, BOB.getUniqueId()));

		assertEquals(List.of(), lobby.ignored(ALICE));
	}
}
