package me.whereareiam.socialismus.testing.sync;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.Module;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Console;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SocialismusRedis on a server, as SocialismusNetwork's transport: it brings its Redis client and tells the admin when it
 * cannot reach Redis.
 */
@Tag("sync")
class RedisModuleTest {
	@Test
	@Socialismus(modules = {Module.NETWORK, Module.REDIS})
	void refusesToLoadWithAReasonWhenRedisCannotBeReached(ScenarioContext anvil) {
		String log = Console.of(anvil).log();

		assertTrue(log.contains("The module socialismus-redis failed to start"),
				"SocialismusRedis started without its Redis:\n" + log);
		assertTrue(log.contains("Failed to connect to Redis"), "SocialismusRedis did not report the unreachable Redis:\n" + log);
		assertFalse(log.contains("Enabled module SocialismusRedis"), "SocialismusRedis was reported as started:\n" + log);
		assertFalse(log.contains("NoClassDefFoundError"), "SocialismusRedis is missing a class:\n" + log);
	}
}
