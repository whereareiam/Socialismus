package me.whereareiam.socialismus.module.network.common.wire;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A logical clock: it counts this node's changes and moves past every counter it sees from another node, so a change
 * made after seeing another always gets a higher counter than it, whatever the nodes' wall clocks say.
 */
public final class NetworkClock {
	private final AtomicLong counter = new AtomicLong();

	/**
	 * Returns the counter of a new change of this node.
	 */
	public long tick() {
		return counter.incrementAndGet();
	}

	/**
	 * Moves the clock past a counter seen from another node.
	 */
	public void observe(long seen) {
		counter.accumulateAndGet(seen, Math::max);
	}
}
