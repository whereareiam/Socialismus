package me.whereareiam.socialismus.module.network.common.replication;

import me.whereareiam.socialismus.module.network.api.model.NodeId;
import org.junit.jupiter.api.Test;

import java.io.UncheckedIOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntryStateTest {
	private static final Revision REVISION = new Revision(7, NodeId.of("lobby"));

	@Test
	void entriesAndRemovalsArriveAsTheyWereSent() {
		EntryState entry = new EntryState("alice", REVISION, new byte[]{4, 2}, 1500);
		EntryState removal = new EntryState("bob", REVISION, null, -1);

		List<EntryState> received = EntryState.decodeAll(EntryState.encodeAll(List.of(entry, removal)));

		assertEquals("alice", received.getFirst().getKey());
		assertEquals(REVISION, received.getFirst().getRevision());
		assertArrayEquals(new byte[]{4, 2}, received.getFirst().getValue());
		assertEquals(1500, received.getFirst().getTtlMs());
		assertTrue(received.getLast().isRemoval());
		assertNull(received.getLast().getValue());
	}

	@Test
	void aChangeCarriesExactlyOneEntry() {
		EntryState entry = new EntryState("alice", REVISION, new byte[]{1}, -1);
		byte[] two = EntryState.encodeAll(List.of(entry, entry));

		assertEquals(entry.getKey(), EntryState.decode(entry.encode()).getKey());
		assertThrows(UncheckedIOException.class, () -> EntryState.decode(two));
		assertThrows(UncheckedIOException.class, () -> EntryState.decodeAll(new byte[]{1}));
	}

	@Test
	void whatAStoreKeepsHasNoExpiry() {
		assertEquals(-1, new EntryState("alice", REVISION, new byte[]{1}, 1500).withoutExpiry().getTtlMs());
	}
}
