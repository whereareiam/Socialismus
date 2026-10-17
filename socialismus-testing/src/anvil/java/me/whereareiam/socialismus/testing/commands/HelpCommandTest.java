package me.whereareiam.socialismus.testing.commands;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The command help, which lists the commands the reader may use.
 */
@Tag("commands")
class HelpCommandTest {
	@Test
	@Socialismus
	void listsTheCommandsThePlayerMayUse(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");

		alice.command("socialismus help");

		String help = alice.sees("Socialismus Command help");
		assertTrue(help.contains("/socialismus help"), "The help does not list its own command: " + help);
		assertFalse(help.contains("/socialismus reload"), "The help lists a command the player may not use: " + help);
	}
}
