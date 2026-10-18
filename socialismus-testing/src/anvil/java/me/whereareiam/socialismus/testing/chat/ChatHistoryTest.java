package me.whereareiam.socialismus.testing.chat;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Consumer;

import static me.whereareiam.socialismus.testing.environment.ServerFiles.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Chat history: who is offered to clear a message from it.
 */
@Tag("chat")
class ChatHistoryTest {
	private static final String CLEAR = "[clear]";

	@Test
	@Socialismus(configure = OverworldClearers.class)
	void onlyThePlayersWhoMeetTheHistoryRequirementsSeeTheClearButton(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");
		Player carol = Player.joins(anvil, "Carol").travelsTo("minecraft:the_nether");

		bob.chat("hello");

		String seenByAlice = alice.sees("[G] Bob: hello");
		String seenByCarol = carol.sees("[G] Bob: hello");
		assertTrue(seenByAlice.contains(CLEAR), "Alice meets the requirements but has no clear button: " + seenByAlice);
		assertFalse(seenByCarol.contains(CLEAR), "Carol is in the nether but has a clear button: " + seenByCarol);
	}

	/**
	 * Global chat offers its messages for clearing to the players of the overworld.
	 */
	static final class OverworldClearers implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(chat("global", 0, "", 0, "{clear}[G] {playerName}: {message}"));
			files.put(CHAT_SETTINGS, Map.of("history", Map.of("requirements", world("world"))));
			files.put(CHAT_MESSAGES, Map.of("clearFormat", Map.of("format", CLEAR + " ")));
		}
	}
}
