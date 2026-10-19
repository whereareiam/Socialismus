package me.whereareiam.socialismus.module.network.common.lease;

import me.whereareiam.socialismus.module.network.api.lease.Leases;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import me.whereareiam.socialismus.module.network.common.TestNetwork;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultLeasesTest {
	private static final String KEY = "alice";

	private final TestNetwork network = new TestNetwork();

	@Test
	void aClaimTakesAFreeKey() {
		Leases a = leases(network.node("a"));
		Leases b = leases(network.node("b"));

		assertTrue(a.claim(KEY, 1));

		assertTrue(a.holds(KEY));
		assertFalse(b.holds(KEY));
		assertEquals(NodeId.of("a"), b.holder(KEY).orElseThrow().getHolder());
	}

	@Test
	void aHigherPriorityTakesAKeyOverAndALowerOneCannot() {
		Leases proxy = leases(network.node("proxy"));
		Leases server = leases(network.node("server"));
		proxy.claim(KEY, 1);

		assertTrue(server.claim(KEY, 10));
		assertFalse(proxy.holds(KEY));
		assertFalse(proxy.claim(KEY, 1));
		assertTrue(server.holds(KEY));
	}

	@Test
	void theKeyOfANodeThatStopsExpiresAndCanBeClaimed() {
		TestNetwork.Node server = network.node("server");
		TestNetwork.Node proxy = network.node("proxy");
		leases(server).claim(KEY, 10);

		server.transport().disconnect();
		network.clock.advance(TestNetwork.LIFETIME.plusSeconds(1));
		proxy.service().maintain();

		assertTrue(leases(proxy).holder(KEY).isEmpty());
		assertTrue(leases(proxy).claim(KEY, 1));
	}

	@Test
	void aRenewedKeyOutlivesItsLifetime() {
		TestNetwork.Node holder = network.node("holder");
		TestNetwork.Node other = network.node("other");
		leases(holder).claim(KEY, 1);

		for (int i = 0; i < 5; i++) {
			network.clock.advance(TestNetwork.LIFETIME.dividedBy(3));
			holder.service().maintain();
			other.service().maintain();
		}

		assertTrue(leases(holder).holds(KEY));
		assertFalse(leases(other).claim(KEY, 1));
	}

	@Test
	void ofTwoCrossingClaimsTheHigherPriorityWins() {
		TestNetwork.Node proxy = network.node("proxy");
		TestNetwork.Node server = network.node("server");

		server.transport().disconnect();
		leases(server).claim(KEY, 10);
		proxy.transport().disconnect();
		server.transport().reconnect();
		leases(proxy).claim(KEY, 1);
		proxy.transport().reconnect();

		assertTrue(leases(server).holds(KEY));
		assertFalse(leases(proxy).holds(KEY));
	}

	@Test
	void aReleasedKeyIsFreeRightAway() {
		Leases a = leases(network.node("a"));
		Leases b = leases(network.node("b"));
		a.claim(KEY, 10);

		a.release(KEY);

		assertTrue(b.claim(KEY, 1));
	}

	@Test
	void tellsListenersWhoTookAKeyOver() {
		Leases proxy = leases(network.node("proxy"));
		Leases server = leases(network.node("server"));
		StringBuilder seen = new StringBuilder();
		proxy.onChange((key, previous, current) -> seen.append(previous == null ? "none" : previous.getHolder())
				.append("->").append(current == null ? "none" : current.getHolder()).append(' '));
		proxy.claim(KEY, 1);

		server.claim(KEY, 10);

		assertEquals("none->proxy proxy->server ", seen.toString());
	}

	private static Leases leases(TestNetwork.Node node) {
		return node.service().leases("test:owners");
	}
}
