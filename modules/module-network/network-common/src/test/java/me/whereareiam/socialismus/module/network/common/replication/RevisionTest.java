package me.whereareiam.socialismus.module.network.common.replication;

import me.whereareiam.socialismus.module.network.api.model.NodeId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RevisionTest {
	@Test
	void theHigherCounterWinsAndTheWriterDecidesATie() {
		Revision lobby = new Revision(5, NodeId.of("lobby"));
		Revision survival = new Revision(5, NodeId.of("survival"));
		Revision later = new Revision(6, NodeId.of("lobby"));

		assertTrue(later.isNewerThan(survival));
		assertTrue(survival.isNewerThan(lobby));
		assertFalse(lobby.isNewerThan(lobby));
	}

	@Test
	void anyRevisionIsNewerThanNone() {
		assertTrue(new Revision(1, NodeId.of("lobby")).isNewerThan(null));
	}
}
