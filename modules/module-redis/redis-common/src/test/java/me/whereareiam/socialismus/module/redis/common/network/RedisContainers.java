package me.whereareiam.socialismus.module.redis.common.network;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.JedisPool;

/**
 * One Redis in a container for the tests of this package, started on first use and stopped with the JVM.
 */
final class RedisContainers {
	private static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
			.withExposedPorts(6379);

	private RedisContainers() {
	}

	static synchronized JedisPool pool() {
		if (!REDIS.isRunning()) REDIS.start();

		return new JedisPool(REDIS.getHost(), REDIS.getFirstMappedPort());
	}

	static GenericContainer<?> container() {
		return REDIS;
	}
}
