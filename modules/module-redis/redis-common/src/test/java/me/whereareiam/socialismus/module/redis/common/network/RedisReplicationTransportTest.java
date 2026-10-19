package me.whereareiam.socialismus.module.redis.common.network;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.module.network.api.transport.ReplicationTransport;
import me.whereareiam.socialismus.module.network.testing.ReplicationTransportContract;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

	/**
	 * When Redis drops every client, the transport subscribes again by itself, tells the network, and carries messages
	 * again.
	 */
	@Test
	void subscribesAgainAndReportsItAfterRedisDroppedTheConnection() throws Exception {
		RedisReplicationTransport sender = new RedisReplicationTransport(RedisContainers.pool());
		RedisReplicationTransport receiver = new RedisReplicationTransport(RedisContainers.pool());
		List<String> received = new CopyOnWriteArrayList<>();
		CountDownLatch reconnected = new CountDownLatch(1);
		receiver.onReconnect(reconnected::countDown);
		receiver.subscribe("reconnect:test", message -> received.add(new String(message, StandardCharsets.UTF_8)));
		Thread.sleep(300);

		RedisContainers.container().execInContainer("redis-cli", "CLIENT", "KILL", "TYPE", "pubsub");

		assertTrue(reconnected.await(15, TimeUnit.SECONDS), "The transport did not report that it reconnected");
		Thread.sleep(300);
		sender.publish("reconnect:test", "after".getBytes(StandardCharsets.UTF_8));
		long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
		while (received.isEmpty() && System.nanoTime() < deadline) Thread.sleep(20);
		assertEquals(List.of("after"), received);
		assertTrue(receiver.isConnected());

		sender.close();
		receiver.close();
	}
}
