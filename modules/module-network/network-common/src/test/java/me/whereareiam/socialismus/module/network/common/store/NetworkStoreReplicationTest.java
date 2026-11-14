package me.whereareiam.socialismus.module.network.common.store;

import me.whereareiam.socialismus.module.network.common.TestNetwork;
import me.whereareiam.socialismus.store.StoreReplication;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NetworkStoreReplicationTest {
	private final TestNetwork network = new TestNetwork();

	@Test
	void aValuePutOnOneNodeIsSharedWithTheOther_AndSoIsItsRemoval() {
		StoreReplication.Entries<String> lobby = entries("lobby");
		StoreReplication.Entries<String> survival = entries("survival");
		List<String> heard = new ArrayList<>();
		survival.onChange((key, previous, current) -> heard.add(key + "=" + current));

		lobby.put("alice", "DIRECT", null);

		assertEquals(Optional.of("DIRECT"), survival.get("alice"));
		assertEquals(Map.of("alice", "DIRECT"), survival.entries());

		lobby.remove("alice");

		assertEquals(Optional.empty(), survival.get("alice"));
		assertEquals(List.of("alice=DIRECT", "alice=null"), heard);
	}

	@Test
	void aStoreKeepsItsOwnCache_SoAModulesCacheOfTheSameNameStaysApart() {
		TestNetwork.Node lobby = network.node("lobby");
		new NetworkStoreReplication(lobby.service().replication()).share("mentionator:modes", String.class).put("alice", "NONE", null);

		assertEquals(Optional.empty(), lobby.service().replication().cache("mentionator:modes", String.class).get("alice"));
		assertEquals(Optional.of("NONE"), lobby.service().replication().cache("stores/mentionator:modes", String.class).get("alice"));
	}

	private StoreReplication.Entries<String> entries(String node) {
		return new NetworkStoreReplication(network.node(node).service().replication()).share("mentionator:modes", String.class);
	}
}
