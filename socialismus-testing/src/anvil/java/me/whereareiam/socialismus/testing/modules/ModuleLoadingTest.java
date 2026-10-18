package me.whereareiam.socialismus.testing.modules;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.Module;
import me.whereareiam.socialismus.testing.environment.ModuleFixture;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Console;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * How Socialismus loads the modules in its {@code modules/} directory: which it accepts, which it refuses, and how
 * a reload reaches their configuration.
 */
@Tag("modules")
class ModuleLoadingTest {
	private static final String DIALOGUE_MESSAGES = "modules/Essentials/features/dialogue/messages.yml";

	@Test
	@Socialismus(modules = {Module.CHIRPER, Module.ESSENTIALS})
	void modulesInTheModulesDirectoryLoad(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();

		String log = Console.of(anvil).log();
		for (String module : new String[]{"Chirper", "Essentials"})
			assertTrue(log.contains("Loaded module " + module), module + " did not load:\n" + log);

		alice.command("socialismus debug");
		alice.seesAll("Chirper", "Essentials");
	}

	@Test
	@Socialismus(modules = Module.ESSENTIALS, moduleFixtures = ModuleFixture.REQUIRES_ANOTHER_VERSION)
	void moduleThatNeedsAnotherSocialismusVersionIsRefused(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();

		String log = Console.of(anvil).log();
		assertTrue(log.contains("Module Chirper requires version of the plugin to be 1\\.0\\.0"),
				"Chirper was not refused with a reason:\n" + log);
		assertFalse(log.contains("Loaded module Chirper"), "Chirper loaded:\n" + log);
		assertTrue(log.contains("Loaded module Essentials"), "The other module did not load:\n" + log);

		alice.command("socialismus debug");
		String debug = alice.sees("Essentials");
		assertFalse(debug.contains("Chirper"), "The debug command listed the refused Chirper:\n" + debug);
	}

	@Test
	@Socialismus(modules = Module.ESSENTIALS)
	void reloadAppliesAChangedModuleConfiguration(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");
		alice.command("message Bob before");
		bob.sees("PM |");

		ServerFiles.replace(anvil, DIALOGUE_MESSAGES, Map.of("commands", Map.of("message", Map.of(
				"recipientFormat", "[changed] a private message"))));
		Console.of(anvil).run("socialismus reload").printed("Configuration reloaded");

		alice.command("message Bob after");
		bob.sees("[changed] a private message");
	}
}
