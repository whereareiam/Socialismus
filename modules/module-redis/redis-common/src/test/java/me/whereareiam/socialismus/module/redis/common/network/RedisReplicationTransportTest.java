package me.whereareiam.socialismus.module.redis.common.network;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.module.network.api.transport.ReplicationTransport;
import me.whereareiam.socialismus.module.network.testing.ReplicationTransportContract;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;

import static org.mockito.Mockito.mock;

@Tag("integration")
class RedisReplicationTransportTest extends ReplicationTransportContract {
	@BeforeAll
	static void initLogger() {
		Logger.init(mock(LoggingHelper.class));
	}

	@Override
	protected ReplicationTransport transport(String node) {
		return new RedisReplicationTransport(RedisContainers.pool());
	}
}
