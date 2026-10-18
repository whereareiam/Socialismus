package me.whereareiam.socialismus.testing.commands;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static me.whereareiam.socialismus.testing.environment.ServerFiles.chat;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * The administrator commands: clearing the chat, reporting what runs, and staying out of reach of other players.
 */
@Tag("commands")
class AdminCommandsTest {
	private static final String[] ADMINISTRATOR_COMMANDS = {"reload", "clear", "debug"};

	@Test
	@Socialismus(configure = GlobalChat.class)
	void clearRemovesThePlayersMessagesFromTheChatOfEveryone(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");
		Player carol = Player.joins(anvil, "Carol").op();
		for (String message : new String[]{"first", "second", "third"}) {
			alice.chat(message);
			alice.sees("[G] Alice: " + message);
		}
		for (String message : new String[]{"spam one", "spam two"}) {
			bob.chat(message);
			alice.sees("[G] Bob: " + message);
		}

		carol.command("socialismus clear Bob");

		carol.sees("Successfully cleared 2x chat history entries");
		alice.sees("[G] Alice: first");
		alice.sees("[G] Alice: third");
		alice.neverSees("spam");
	}

	@Test
	@Socialismus
	void debugReportsThePlatformAndVersion(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();

		alice.command("socialismus debug");

		alice.seesAll("Plugin version:", "Server platform: PAPER", "Plugin platform: PAPER");
	}

	@Test
	@Socialismus
	void playerWithoutPermissionNeitherSeesNorRunsTheAdministratorCommands(ScenarioContext anvil) {
		Player bob = Player.joins(anvil, "Bob");

		bob.command("socialismus help");
		String help = bob.sees("Socialismus Command help");
		for (String command : ADMINISTRATOR_COMMANDS)
			assertFalse(help.contains("/socialismus " + command), "The help lists /socialismus " + command + ": " + help);

		for (String command : ADMINISTRATOR_COMMANDS) {
			bob.command("socialismus " + command);
			bob.sees("permission to use this command");
		}
		bob.neverSees("Plugin version:");
	}

	static final class GlobalChat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(chat("global", 0, "", 0, "[G] {playerName}: {message}"));
		}
	}
}
