package me.whereareiam.socialismus.testing.dialogue;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.Module;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * What draws a recipient's attention to a private message besides the message: the alert of the format they see.
 */
@Tag("dialogue")
class DialogueAlertTest {
	@Test
	@Socialismus(modules = Module.DIALOGUE)
	void recipientHearsTheDefaultAlert(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");

		alice.command("message Bob hello");

		bob.sees("Alice -> Bob: hello");
		bob.hears("minecraft:entity.experience_orb.pickup");
	}

	@Test
	@Socialismus(modules = Module.DIALOGUE, configure = TitleAndActionBar.class)
	void recipientSeesTheActionBarAndTitleOfTheirFormat(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");

		alice.command("message Bob <red>hello");

		bob.seesActionBar("Alice wrote: <red>hello");
		bob.seesTitle("New message from Alice");
	}

	/**
	 * The recipient's format shows an action bar and a title with the message.
	 */
	static final class TitleAndActionBar implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.put(DialogueFiles.FORMATS, Map.of("received", List.of(Map.of(
					"format", "[PM] {senderName}: {message}",
					"alert", Map.of(
							"actionBar", "{senderName} wrote: {message}",
							"title", Map.of("title", "New message from {senderName}")
					)
			))));
		}
	}
}
