package me.whereareiam.socialismus.testing.modules;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.ModuleFixture;
import me.whereareiam.socialismus.testing.environment.Plugin;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Console;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What a module reaches of what it depends on: the exports of another module, and the classes of a plugin, and that
 * it stops before the module it depends on.
 */
@Tag("modules")
class ModuleDependencyTest {
	@Test
	@Socialismus(
			plugins = Plugin.PACKETEVENTS,
			moduleFixtures = {ModuleFixture.JOURNEY_LIBRARY, ModuleFixture.JOURNEY_CONSUMER}
	)
	void aModuleReachesTheExportsOfAModuleAndTheClassesOfAPlugin(ScenarioContext anvil) {
		Player.joins(anvil, "Alice");

		String log = Console.of(anvil).log();
		assertTrue(log.contains("Loaded module JourneyConsumer"), "JourneyConsumer did not load:\n" + log);
		assertTrue(log.contains("JourneyConsumer greets: hello from JourneyLibrary"),
				"JourneyConsumer did not reach the library's export:\n" + log);
		assertTrue(log.contains("JourneyConsumer reaches the library's internals: false"),
				"JourneyConsumer reached a package the library does not export:\n" + log);
		assertTrue(log.contains("JourneyConsumer reaches PacketEvents: true"),
				"JourneyConsumer did not reach the plugin it depends on:\n" + log);
	}

	@Test
	@Socialismus(
			plugins = Plugin.PACKETEVENTS,
			moduleFixtures = {ModuleFixture.JOURNEY_LIBRARY, ModuleFixture.JOURNEY_CONSUMER}
	)
	void aModuleStopsBeforeTheModuleItDependsOn(ScenarioContext anvil) {
		Player.joins(anvil, "Alice");

		Console.of(anvil).run("stop").printed("JourneyConsumer stops while the library serves: true");
	}
}
