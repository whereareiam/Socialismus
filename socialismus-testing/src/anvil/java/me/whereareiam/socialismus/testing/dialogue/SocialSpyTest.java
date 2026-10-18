package me.whereareiam.socialismus.testing.dialogue;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.Module;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Staff who spy see the private messages of others, unless privacy mode is on.
 */
@Tag("dialogue")
class SocialSpyTest {
	@Test
	@Socialismus(modules = Module.DIALOGUE)
	void spySeesThePrivateMessagesOfOthers(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");
		Player staff = Player.joins(anvil, "Staff").op();
		Player carol = Player.joins(anvil, "Carol");

		staff.command("socialspy");
		staff.sees("You now see the private messages of others");

		alice.command("message Bob between us");
		staff.sees("[Spy] Alice -> Bob: between us");
		carol.neverSees("between us");
	}

	@Test
	@Socialismus(modules = Module.DIALOGUE, configure = PrivacyMode.class)
	void privacyModeKeepsSpiesOut(ScenarioContext anvil) {
		Player staff = Player.joins(anvil, "Staff").op();

		staff.command("socialspy");

		staff.sees("Private messages cannot be seen by others here");
	}

	static final class PrivacyMode implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.put(DialogueFiles.SETTINGS, Map.of("moderation", Map.of("privacy", true)));
		}
	}
}
