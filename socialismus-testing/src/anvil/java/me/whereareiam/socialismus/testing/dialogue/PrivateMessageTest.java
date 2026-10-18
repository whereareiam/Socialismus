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
 * Dialogue's private messages between two players on one server.
 */
@Tag("dialogue")
class PrivateMessageTest {
	@Test
	@Socialismus(modules = Module.DIALOGUE)
	void messageReachesTheRecipientAndIsConfirmedToTheSender(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");

		alice.command("message Bob hello");

		bob.sees("Alice -> Bob: hello");
		alice.sees("Alice -> Bob: hello");
		alice.neverSees("An error occurred");
	}

	@Test
	@Socialismus(modules = Module.DIALOGUE)
	void replyAnswersTheLastConversationPartner(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");
		Player carol = Player.joins(anvil, "Carol");
		alice.command("message Bob from Alice");
		bob.sees("Alice -> Bob: from Alice");
		carol.command("message Bob from Carol");
		bob.sees("Carol -> Bob: from Carol");

		bob.command("reply thanks");

		carol.sees("Bob -> Carol: thanks");
		bob.sees("Bob -> Carol: thanks");
		alice.neverSees("thanks");
	}

	@Test
	@Socialismus(modules = Module.DIALOGUE, configure = KnownNotices.class)
	void messageToAnOfflinePlayerFailsWithAReason(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");

		alice.command("message Zed are you there");

		alice.sees("Nobody named Zed is online");
	}

	@Test
	@Socialismus(modules = Module.DIALOGUE, configure = KnownNotices.class)
	void replyWithoutAConversationFailsWithAReason(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");

		alice.command("reply anyone there");

		alice.sees("Nobody to reply to");
	}

	static final class KnownNotices implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.put(DialogueFiles.MESSAGES, Map.of("messaging", Map.of(
					"noRecipient", "Nobody named {recipientName} is online",
					"reply", Map.of("noConversations", "Nobody to reply to"))));
		}
	}
}
