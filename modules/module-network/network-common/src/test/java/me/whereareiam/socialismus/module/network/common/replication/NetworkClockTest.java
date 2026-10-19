package me.whereareiam.socialismus.module.network.common.replication;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NetworkClockTest {
	@Test
	void aChangeAfterSeeingAnotherNodesCounterGetsAHigherOne() {
		NetworkClock clock = new NetworkClock();
		clock.tick();

		clock.observe(41);

		assertEquals(42, clock.tick());
	}

	@Test
	void anOlderCounterLeavesTheClockAsItIs() {
		NetworkClock clock = new NetworkClock();
		clock.observe(10);

		clock.observe(3);

		assertEquals(11, clock.tick());
	}
}
