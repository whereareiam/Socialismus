package me.whereareiam.socialismus.module.network.common.replication;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A logical clock: it counts this node's changes and moves past every counter it sees from another node, so a change
 * made after seeing another always gets a higher counter than it, whatever the nodes' wall clocks say.
 */
final class NetworkClock {
	private final AtomicLong counter = new AtomicLong();

	/**
	 * Returns the counter of a new change of this node.
	 *
	 * @return the counter
	 */
	long tick() {
		return counter.incrementAndGet();
	}

	/**
	 * Moves the clock past a counter seen from another node.
	 *
	 * @param seen the other node's counter
	 */
	void observe(long seen) {
		counter.accumulateAndGet(seen, Math::max);
	}
}
