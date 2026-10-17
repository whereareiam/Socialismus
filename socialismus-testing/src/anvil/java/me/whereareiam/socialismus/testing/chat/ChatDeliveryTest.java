package me.whereareiam.socialismus.testing.chat;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static me.whereareiam.socialismus.testing.environment.ServerFiles.chat;

/**
 * How a chat message reaches players: in the format of the chat it matched, through the chat its symbol selects,
 * and only as far as the chat's radius.
 */
@Tag("chat")
class ChatDeliveryTest {
	@Test
	@Socialismus(configure = GlobalChat.class)
	void messageReachesEveryPlayerInTheFormatOfItsChat(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");

		alice.chat("hello everyone");

		bob.sees("[G] Alice: hello everyone");
		alice.sees("[G] Alice: hello everyone");
	}

	@Test
	@Socialismus(configure = LocalAndGlobalChats.class)
	void symbolSelectsItsChatAndIsStrippedFromTheMessage(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");

		alice.chat("!to everyone");
		bob.sees("[G] Alice: to everyone");

		alice.chat("to those nearby");
		bob.sees("[L] Alice: to those nearby");
		bob.neverSees("!to everyone");
	}

	@Test
	@Socialismus(configure = LocalAndGlobalChats.class)
	void localChatReachesOnlyThePlayersWithinItsRadius(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");
		Player carol = Player.joins(anvil, "Carol").teleport(500, 100, 500);

		alice.chat("only nearby");

		bob.sees("[L] Alice: only nearby");
		carol.neverSees("only nearby");
	}

	static final class GlobalChat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(chat("global", 0, "", 0, "<gray>[G] {playerName}: <white>{message}"));
		}
	}

	/**
	 * Local chat reaches 100 blocks; a message starting with {@code !} goes to global chat.
	 */
	static final class LocalAndGlobalChats implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(
					chat("local", 0, "", 100, "<gray>[L] {playerName}: <white>{message}"),
					chat("global", 1, "!", 0, "<gray>[G] {playerName}: <white>{message}")
			);
		}
	}
}
