package me.whereareiam.socialismus.module.redis.common.network;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.module.network.api.model.NetworkNode;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import me.whereareiam.socialismus.module.network.api.replication.ReplicatedCache;
import me.whereareiam.socialismus.module.network.api.replication.Versioned;
import me.whereareiam.socialismus.module.network.api.type.NodeRole;
import me.whereareiam.socialismus.module.network.common.DefaultNetworkService;
import me.whereareiam.socialismus.module.network.common.replication.DefaultReplicationSystem;
import me.whereareiam.socialismus.module.network.testing.JsonSerialization;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * SocialismusNetwork replicating through SocialismusRedis: nodes of their own, each with its own transport, on one
 * Redis.
 */
@Tag("integration")
class RedisNetworkTest {
	private final String cache = "test:" + UUID.randomUUID();
	private final List<AutoCloseable> opened = new ArrayList<>();

	@BeforeAll
	static void initLogger() {
		Logger.init(mock(LoggingHelper.class));
	}

	@AfterEach
	void close() throws Exception {
		for (AutoCloseable closeable : opened) closeable.close();
	}

	@Test
	void aChangeReachesTheOtherNode() throws Exception {
		ReplicatedCache<String> a = node("a").replication().cache(cache, String.class);
		ReplicatedCache<String> b = node("b").replication().cache(cache, String.class);

		a.put("alice", "spying").join();

		awaitValue(() -> b.get("alice"), "spying");
	}

	@Test
	void ofTwoConditionalWritesOnlyOneSucceeds() throws Exception {
		ReplicatedCache<String> a = node("a").replication().cache(cache, String.class);
		ReplicatedCache<String> b = node("b").replication().cache(cache, String.class);
		a.put("alice", "one").join();
		awaitValue(() -> b.get("alice"), "one");
		Versioned<String> readByA = a.read("alice").orElseThrow();
		Versioned<String> readByB = b.read("alice").orElseThrow();

		boolean aWrote = a.write("alice", readByA, "from a", null).join();
		boolean bWrote = b.write("alice", readByB, "from b", null).join();

		assertTrue(aWrote);
		assertFalse(bWrote);
		awaitValue(() -> b.get("alice"), "from a");
	}

	@Test
	void aNodeThatStartsLaterLoadsFromRedis() throws Exception {
		node("a").replication().cache(cache, String.class).put("alice", "stored").join();

		ReplicatedCache<String> late = node("late").replication().cache(cache, String.class);

		awaitValue(() -> late.get("alice"), "stored");
	}

	private DefaultNetworkService node(String name) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		opened.add(executor::shutdownNow);
		NodeId id = NodeId.of(name + "-" + UUID.randomUUID().toString().substring(0, 8));
		DefaultReplicationSystem replication = new DefaultReplicationSystem(id, Clock.systemUTC(), new JsonSerialization(), executor);
		DefaultNetworkService service = new DefaultNetworkService(replication,
				NetworkNode.builder().id(id).role(NodeRole.SERVER).version("dev").build(), Duration.ofSeconds(15));
		RedisReplicationTransport transport = new RedisReplicationTransport(RedisContainers.pool());
		opened.add(transport);
		service.attach(transport, new RedisReplicationStore(RedisContainers.pool()));
		sleep(300);

		return service;
	}

	private static void awaitValue(Supplier<Optional<String>> read, String expected) throws InterruptedException {
		long deadline = System.nanoTime() + Duration.ofSeconds(10).toNanos();
		while (!Optional.of(expected).equals(read.get()) && System.nanoTime() < deadline) Thread.sleep(20);

		assertEquals(Optional.of(expected), read.get());
	}

	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException interrupted) {
			Thread.currentThread().interrupt();
		}
	}
}
