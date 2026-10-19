package me.whereareiam.socialismus.module.network.common.replication;

import me.whereareiam.socialismus.module.network.api.replication.ReplicatedCache;
import me.whereareiam.socialismus.module.network.api.replication.Versioned;
import me.whereareiam.socialismus.module.network.common.TestNetwork;
import me.whereareiam.socialismus.module.network.testing.InMemoryStore;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultReplicatedCacheTest {
	private static final String CACHE = "test:spies";

	private final TestNetwork network = new TestNetwork();

	@Test
	void anEntryReachesEveryNode() {
		ReplicatedCache<String> a = cache(network.node("a"));
		ReplicatedCache<String> b = cache(network.node("b"));

		a.put("alice", "spying");

		assertEquals(Optional.of("spying"), b.get("alice"));
		assertEquals(Map.of("alice", "spying"), b.entries());
	}

	@Test
	void aRemovalReachesEveryNode() {
		ReplicatedCache<String> a = cache(network.node("a"));
		ReplicatedCache<String> b = cache(network.node("b"));
		a.put("alice", "spying");

		b.remove("alice");

		assertTrue(a.get("alice").isEmpty());
	}

	@Test
	void aNodeThatWasAwayCatchesUpWithoutBringingItsOldValueBack() {
		TestNetwork.Node nodeA = network.node("a");
		TestNetwork.Node nodeB = network.node("b");
		ReplicatedCache<String> a = cache(nodeA);
		ReplicatedCache<String> b = cache(nodeB);
		a.put("alice", "old");

		nodeB.transport().disconnect();
		a.put("alice", "new");
		a.put("bob", "added while b was away");
		nodeB.transport().reconnect();

		assertEquals(Optional.of("new"), b.get("alice"));
		assertEquals(Optional.of("added while b was away"), b.get("bob"));
		assertEquals(Optional.of("new"), a.get("alice"));
	}

	@Test
	void aRemovalWinsOverAnOlderChangeArrivingLater() {
		TestNetwork.Node nodeA = network.node("a");
		TestNetwork.Node nodeB = network.node("b");
		ReplicatedCache<String> a = cache(nodeA);
		ReplicatedCache<String> b = cache(nodeB);
		a.put("alice", "spying");

		nodeA.transport().disconnect();
		b.remove("alice");
		nodeA.transport().reconnect();

		assertTrue(a.get("alice").isEmpty());
		assertTrue(b.get("alice").isEmpty());
	}

	@Test
	void aNodeThatStartsLaterReceivesWhatTheOthersHold() {
		ReplicatedCache<String> a = cache(network.node("a"));
		a.put("alice", "spying");

		ReplicatedCache<String> late = cache(network.node("late"));

		assertEquals(Optional.of("spying"), late.get("alice"));
	}

	@Test
	void anEntryExpiresOnEveryNode() {
		ReplicatedCache<String> a = cache(network.node("a"));
		ReplicatedCache<String> b = cache(network.node("b"));
		a.put("alice", "briefly", Duration.ofSeconds(30));

		network.clock.advance(Duration.ofSeconds(31));

		assertTrue(a.get("alice").isEmpty());
		assertTrue(b.get("alice").isEmpty());
	}

	@Test
	void tellsListenersWhichNodeChangedAnEntry() {
		ReplicatedCache<String> a = cache(network.node("a"));
		ReplicatedCache<String> b = cache(network.node("b"));
		List<String> seen = new ArrayList<>();
		b.onChange((key, previous, current, origin) -> seen.add(key + ":" + previous + "->" + current + "@" + origin));

		a.put("alice", "one");
		b.put("alice", "two");

		assertEquals(List.of("alice:null->one@remote:a", "alice:one->two@local"), seen);
	}

	@Test
	void writesConditionallyOnlyWhileTheEntryIsAsRead() {
		ReplicatedCache<String> a = cache(network.node("a"));
		a.put("alice", "one");
		Versioned<String> read = a.read("alice").orElseThrow();

		assertTrue(a.write("alice", read, "two", null).join());
		assertFalse(a.write("alice", read, "three", null).join());
		assertEquals(Optional.of("two"), a.get("alice"));
		assertFalse(a.write("alice", null, "four", null).join());
	}

	@Test
	void ofTwoConditionalWritesThroughAStoreOnlyOneSucceeds() {
		InMemoryStore store = new InMemoryStore(network.clock);
		ReplicatedCache<String> a = cache(network.node("a", store));
		ReplicatedCache<String> b = cache(network.node("b", store));
		a.put("alice", "one").join();
		Versioned<String> readByA = a.read("alice").orElseThrow();
		Versioned<String> readByB = b.read("alice").orElseThrow();

		boolean aWrote = a.write("alice", readByA, "from a", null).join();
		boolean bWrote = b.write("alice", readByB, "from b", null).join();

		assertTrue(aWrote);
		assertFalse(bWrote);
		assertEquals(Optional.of("from a"), b.get("alice"));
	}

	@Test
	void aNodeLoadsFromTheStoreWhenItStarts() {
		InMemoryStore store = new InMemoryStore(network.clock);
		TestNetwork.Node nodeA = network.node("a", store);
		cache(nodeA).put("alice", "stored").join();
		nodeA.transport().disconnect();

		ReplicatedCache<String> late = cache(network.node("late", store));

		assertEquals(Optional.of("stored"), late.get("alice"));
	}

	@Test
	void aLargeSnapshotIsSplitToFitTheTransport() {
		TestNetwork small = new TestNetwork(4096);
		ReplicatedCache<String> a = small.node("a").service().replication().cache(CACHE, String.class);
		for (int i = 0; i < 200; i++) a.put("player-" + i, "x".repeat(100));

		ReplicatedCache<String> late = small.node("late").service().replication().cache(CACHE, String.class);

		assertEquals(200, late.entries().size());
	}

	@Test
	void refusesOneNameForTwoTypes() {
		TestNetwork.Node node = network.node("a");
		cache(node);

		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
				() -> node.service().replication().cache(CACHE, Integer.class));
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
				() -> node.service().replication().channel(CACHE, String.class));
	}

	private static ReplicatedCache<String> cache(TestNetwork.Node node) {
		return node.service().replication().cache(CACHE, String.class);
	}
}
