package me.whereareiam.socialismus.testing.dialogue;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.Module;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static me.whereareiam.socialismus.testing.environment.ServerFiles.chat;

/**
 * Conversation mode: naming a player without a message makes chat go to them as private messages.
 */
@Tag("dialogue")
class ConversationModeTest {
	@Test
	@Socialismus(modules = Module.DIALOGUE, configure = GlobalChat.class)
	void chatGoesToThePartnerUntilThePlayerLeavesTheConversation(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");
		Player carol = Player.joins(anvil, "Carol");

		alice.command("message Bob");
		alice.sees("You now write to Bob in chat");

		alice.chat("just for you");
		bob.sees("Alice -> Bob: just for you");
		carol.neverSees("just for you");

		alice.command("message Bob");
		alice.sees("You no longer write to Bob in chat");

		alice.chat("for everyone");
		carol.sees("[G] Alice: for everyone");
	}

	static final class GlobalChat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(chat("global", 0, "", 0, "[G] {playerName}: {message}"));
		}
	}
}
