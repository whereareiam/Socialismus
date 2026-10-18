package me.whereareiam.socialismus.testing.chirper;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.Module;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * The example announcements Chirper writes on its first start.
 */
@Tag("chirper")
class ExampleAnnouncementTest {
	@Test
	@Socialismus(modules = Module.CHIRPER)
	void exampleTitleAnnouncementPlaysItsSound(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();

		alice.command("socialismus announce titleExample1");

		alice.seesTitle("whereareiam is now a neko!");
		alice.hears("entity.player.levelup");
	}
}
