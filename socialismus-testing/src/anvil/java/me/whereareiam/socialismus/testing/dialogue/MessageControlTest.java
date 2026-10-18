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

import static me.whereareiam.socialismus.testing.environment.ServerFiles.chat;

/**
 * What players decide about their own private messages: turning them off and ignoring others.
 */
@Tag("dialogue")
class MessageControlTest {
	@Test
	@Socialismus(modules = Module.DIALOGUE)
	void playerWhoTurnedMessagesOffReceivesNoneButFromSendersWhoBypassIt(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");
		Player carol = Player.joins(anvil, "Carol").op();

		bob.command("msgtoggle");
		bob.sees("You no longer receive private messages");

		alice.command("message Bob are you there");
		alice.sees("Bob does not receive private messages");
		bob.neverSees("are you there");

		carol.command("message Bob from the staff");
		bob.sees("Carol -> Bob: from the staff");
	}

	@Test
	@Socialismus(modules = Module.DIALOGUE)
	void ignoredPlayersMessagesDoNotArrive(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");

		bob.command("ignore Alice");
		bob.sees("You now ignore Alice");

		alice.command("message Bob hello");
		alice.sees("Bob does not receive your private messages");
		bob.neverSees("hello");

		bob.command("ignore");
		bob.seesAll("You ignore:", "Alice");
	}

	@Test
	@Socialismus(modules = Module.DIALOGUE, configure = IgnoringIncludesChat.class)
	void ignoringCanHideAPlayersChatToo(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");
		Player carol = Player.joins(anvil, "Carol");

		bob.command("ignore Alice");
		bob.sees("You now ignore Alice");

		alice.chat("can you hear me");
		carol.sees("[G] Alice: can you hear me");
		bob.neverSees("can you hear me");
	}

	/**
	 * Ignoring a player hides their public chat as well, in a global chat.
	 */
	static final class IgnoringIncludesChat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(chat("global", 0, "", 0, "[G] {playerName}: {message}"));
			files.put(DialogueFiles.SETTINGS, Map.of("control", Map.of("ignore", Map.of("includesChat", true))));
		}
	}
}
