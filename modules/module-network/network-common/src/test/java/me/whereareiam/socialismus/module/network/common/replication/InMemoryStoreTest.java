package me.whereareiam.socialismus.module.network.common.replication;

import me.whereareiam.socialismus.module.network.api.transport.ReplicationStore;
import me.whereareiam.socialismus.module.network.testing.InMemoryStore;
import me.whereareiam.socialismus.module.network.testing.MutableClock;
import me.whereareiam.socialismus.module.network.testing.ReplicationStoreContract;

import java.time.Duration;

class InMemoryStoreTest extends ReplicationStoreContract {
	private final MutableClock clock = new MutableClock();
	private final InMemoryStore store = new InMemoryStore(clock);

	@Override
	protected ReplicationStore store() {
		return store;
	}

	@Override
	protected void pass(Duration duration) {
		clock.advance(duration);
	}
}
