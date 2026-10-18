package me.whereareiam.socialismus.testing.commands;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import me.whereareiam.socialismus.type.requirement.RequirementOperatorType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Consumer;

import static me.whereareiam.socialismus.testing.environment.ServerFiles.CHAT_SETTINGS;
import static me.whereareiam.socialismus.testing.environment.ServerFiles.chat;
import static me.whereareiam.socialismus.testing.environment.ServerFiles.world;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
		assertFalse(alice.chatLines().contains("spam"), "Bob's messages are still in Alice's chat");
		assertTrue(alice.chatLines().contains("[G] Alice: second"), "Alice's own messages left her chat");
	}

	@Test
	@Socialismus(configure = NetherProtected.class)
	void clearKeepsTheMessagesOfAPlayerWhoMeetsTheBypassRequirements(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob").travelsTo("minecraft:the_nether");
		Player carol = Player.joins(anvil, "Carol").op();
		for (String message : new String[]{"first", "second", "third"}) {
			alice.chat(message);
			carol.sees("[G] Alice: " + message);
		}
		for (String message : new String[]{"from below", "still below"}) {
			bob.chat(message);
			carol.sees("[G] Bob: " + message);
		}

		carol.command("socialismus clear Bob");
		carol.sees("You can't clear chat history from this user");
		assertTrue(alice.chatLines().contains("[G] Bob: from below"), "Bob's messages left Alice's chat");

		carol.command("socialismus clear Alice");
		carol.sees("Successfully cleared 3x chat history entries");
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

	/**
	 * Global chat, whose history keeps the messages of the players outside the overworld from clearing.
	 */
	static final class NetherProtected implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(chat("global", 0, "", 0, "[G] {playerName}: {message}"));
			RequirementGroup outsideTheOverworld = world("world").toBuilder()
					.operator(RequirementOperatorType.NOT)
					.build();
			files.put(CHAT_SETTINGS, Map.of("history", Map.of("bypassRequirements", outsideTheOverworld)));
		}
	}
}
