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

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * How Socialismus loads the modules in its {@code modules/} directory: which it accepts, which it refuses, and how
 * a reload reaches their configuration.
 */
@Tag("modules")
class ModuleLoadingTest {
	private static final String DIALOGUE_FORMATS = "modules/dialogue/formats/messaging.yml";

	@Test
	@Tag("compatibility")
	@Socialismus(modules = {Module.CHIRPER, Module.DIALOGUE})
	void modulesInTheModulesDirectoryLoad(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();

		String log = Console.of(anvil).log();
		for (String module : new String[]{"Chirper", "Dialogue"})
			assertTrue(log.contains("Enabled module " + module), module + " did not start:\n" + log);

		alice.command("socialismus debug");
		alice.seesAll("Chirper", "Dialogue");
	}

	@Test
	@Socialismus(modules = Module.DIALOGUE, moduleFixtures = ModuleFixture.REQUIRES_ANOTHER_VERSION)
	void moduleThatNeedsAnotherSocialismusVersionIsRefused(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();

		String log = Console.of(anvil).log();
		assertTrue(log.contains("The module chirper cannot start: It is built for the host's API ^1.0.0"),
				"Chirper was not refused with a reason:\n" + log);
		assertFalse(log.contains("Enabled module Chirper"), "Chirper started:\n" + log);
		assertTrue(log.contains("Enabled module Dialogue"), "The other module did not start:\n" + log);

		alice.command("socialismus debug");
		String debug = alice.sees("Dialogue");
		assertFalse(debug.contains("Chirper"), "The debug command listed the refused Chirper:\n" + debug);
	}

	@Test
	@Tag("compatibility")
	@Socialismus(modules = Module.DIALOGUE, moduleFixtures = ModuleFixture.FOR_ANOTHER_PLATFORM)
	void moduleForAnotherPlatformIsRefused(ScenarioContext anvil) {
		Player.joins(anvil, "Alice");

		String log = Console.of(anvil).log();
		assertTrue(log.contains("The module chirper cannot start: It runs on velocity, not on paper"),
				"Chirper was not refused with a reason:\n" + log);
		assertFalse(log.contains("Enabled module Chirper"), "Chirper started:\n" + log);
		assertTrue(log.contains("Enabled module Dialogue"), "The other module did not start:\n" + log);
	}

	@Test
	@Socialismus(modules = Module.DIALOGUE)
	void reloadAppliesAChangedModuleConfiguration(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");
		alice.command("message Bob before");
		bob.sees("PM |");

		ServerFiles.replace(anvil, DIALOGUE_FORMATS, Map.of("received", List.of(Map.of(
				"format", "[changed] a private message"))));
		Console.of(anvil).run("socialismus reload").printed("Configuration reloaded");

		alice.command("message Bob after");
		bob.sees("[changed] a private message");
	}
}
