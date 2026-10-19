package me.whereareiam.socialismus.module.dialogue.network.store;

import me.whereareiam.socialismus.module.dialogue.api.model.DialoguePreferences;
import me.whereareiam.socialismus.module.dialogue.network.TestNodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReplicatedPreferencesStoreTest {
	private static final UUID ALICE = UUID.randomUUID();

	private final TestNodes nodes = new TestNodes();

	@Test
	void aChangeOnOneNodeHoldsOnEveryNode() {
		ReplicatedPreferencesStore lobby = new ReplicatedPreferencesStore(nodes.node("lobby").replication());
		ReplicatedPreferencesStore survival = new ReplicatedPreferencesStore(nodes.node("survival").replication());

		DialoguePreferences changed = lobby.update(ALICE, current -> current.withMessagesOff(true));

		assertTrue(changed.isMessagesOff());
		assertTrue(survival.get(ALICE).isMessagesOff());
	}

	@Test
	void changesOfDifferentPreferencesOnTwoNodesBothHold() {
		ReplicatedPreferencesStore lobby = new ReplicatedPreferencesStore(nodes.node("lobby").replication());
		ReplicatedPreferencesStore survival = new ReplicatedPreferencesStore(nodes.node("survival").replication());

		lobby.update(ALICE, current -> current.withMessagesOff(true));
		survival.update(ALICE, current -> current.withSpying(true));

		DialoguePreferences seen = lobby.get(ALICE);
		assertTrue(seen.isMessagesOff());
		assertTrue(seen.isSpying());
	}

	@Test
	void undoingEveryPreferenceLeavesThePlayerWithNone() {
		ReplicatedPreferencesStore lobby = new ReplicatedPreferencesStore(nodes.node("lobby").replication());
		ReplicatedPreferencesStore survival = new ReplicatedPreferencesStore(nodes.node("survival").replication());
		lobby.update(ALICE, current -> current.withSpying(true));

		DialoguePreferences undone = survival.update(ALICE, current -> current.withSpying(false));

		assertEquals(DialoguePreferences.NONE, undone);
		assertFalse(lobby.get(ALICE).isSpying());
	}
}
