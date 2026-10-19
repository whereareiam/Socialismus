package me.whereareiam.socialismus.module.redis.common.network;

import me.whereareiam.socialismus.module.network.api.transport.ReplicationStore;
import me.whereareiam.socialismus.module.network.testing.ReplicationStoreContract;
import org.junit.jupiter.api.Tag;

import java.time.Duration;

@Tag("integration")
class RedisReplicationStoreTest extends ReplicationStoreContract {
	private final ReplicationStore store = new RedisReplicationStore(RedisContainers.pool());

	@Override
	protected ReplicationStore store() {
		return store;
	}

	@Override
	protected void pass(Duration duration) throws InterruptedException {
		Thread.sleep(duration.toMillis());
	}
}
