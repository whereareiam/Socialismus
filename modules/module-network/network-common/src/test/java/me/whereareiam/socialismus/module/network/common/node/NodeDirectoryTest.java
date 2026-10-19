package me.whereareiam.socialismus.module.network.common.node;

import me.whereareiam.socialismus.module.network.api.model.NetworkNode;
import me.whereareiam.socialismus.module.network.common.TestNetwork;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NodeDirectoryTest {
	private final TestNetwork network = new TestNetwork();

	@Test
	void everyNodeKnowsTheOthers() {
		TestNetwork.Node a = network.node("a");
		network.node("b");
		TestNetwork.Node c = network.node("c");

		assertEquals(Set.of("a", "b", "c"), ids(a));
		assertEquals(Set.of("a", "b", "c"), ids(c));
	}

	@Test
	void aNodeThatStopsAnnouncingLeavesWithinOneLifetime() {
		TestNetwork.Node a = network.node("a");
		TestNetwork.Node b = network.node("b");

		b.transport().disconnect();
		network.clock.advance(TestNetwork.LIFETIME.plusSeconds(1));
		a.service().maintain();

		assertEquals(Set.of("a"), ids(a));
	}

	@Test
	void aNodeThatLeavesIsGoneRightAway() {
		TestNetwork.Node a = network.node("a");
		TestNetwork.Node b = network.node("b");

		b.service().leave();

		assertEquals(Set.of("a"), ids(a));
	}

	@Test
	void anUpdatedAnnouncementReachesTheOthers() {
		TestNetwork.Node a = network.node("a");
		TestNetwork.Node b = network.node("b");

		b.service().directory().update(b.service().self().toBuilder().server("lobby").build());

		NetworkNode seen = a.service().nodes().stream().filter(node -> node.getId().value().equals("b")).findFirst().orElseThrow();
		assertEquals("lobby", seen.getServer());
	}

	private static Set<String> ids(TestNetwork.Node node) {
		return node.service().nodes().stream().map(n -> n.getId().value()).collect(Collectors.toSet());
	}
}
