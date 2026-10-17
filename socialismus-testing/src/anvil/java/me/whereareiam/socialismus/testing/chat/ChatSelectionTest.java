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

/**
 * Which chat a message goes to: by priority, by its triggers, and only among the chats that are enabled.
 */
@Tag("chat")
class ChatSelectionTest {
	private static final String NO_CHAT = "No chat takes this message";

	@Test
	@Socialismus(configure = StaffAndGlobalOnOneSymbol.class)
	void chatWithTheHighestPriorityWinsWhenSeveralMatch(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice").op();
		Player bob = Player.joins(anvil, "Bob");

		alice.chat("!from the staff");
		bob.sees("[S] Alice: from the staff");

		bob.chat("!from a player");
		alice.sees("[G] Bob: from a player");
	}

	@Test
	@Socialismus(configure = QuestionChat.class)
	void regexTriggerSendsToItsChat(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");

		alice.chat("is anyone here?");
		bob.sees("[Q] Alice: is anyone here?");

		alice.chat("hello");
		bob.sees("[G] Alice: hello");
	}

	@Test
	@Socialismus(configure = DisabledGlobalChat.class)
	void disabledChatIsSkipped(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");

		alice.chat("hello");

		bob.sees("[L] Alice: hello");
		bob.neverSees("[G] Alice: hello");
	}

	@Test
	@Socialismus(configure = OnlySymbolChat.class)
	void messageThatMatchesNoChatGetsTheNoChatMessage(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");

		alice.chat("no symbol");

		alice.sees(NO_CHAT);
		bob.neverSees("no symbol");
	}

	@Test
	@Socialismus(configure = ChatsInTwoFiles.class)
	void chatsFromSeveralFilesAreAllLoaded(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");

		alice.chat("from the first file");
		bob.sees("[G] Alice: from the first file");

		alice.chat("!from the second file");
		bob.sees("[A] Alice: from the second file");
	}

	/**
	 * Staff chat and global chat both take {@code !}; staff chat, the higher, takes only its permission's holders.
	 */
	static final class StaffAndGlobalOnOneSymbol implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(
					chat("global", 1, "!", 0, "[G] {playerName}: {message}"),
					chat("staff", 2, "!", 0, "[S] {playerName}: {message}").toBuilder()
							.requirements(requirements(permission("socialismus.chat.staff"), null))
							.build()
			);
		}
	}

	/**
	 * A message ending in a question mark goes to question chat; every other one to global chat.
	 */
	static final class QuestionChat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(
					chat("global", 0, "", 0, "[G] {playerName}: {message}"),
					regexChat("question", 1, ".*\\?", "[Q] {playerName}: {message}")
			);
		}
	}

	/**
	 * Global chat would win over local chat, but it is disabled.
	 */
	static final class DisabledGlobalChat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(
					chat("local", 0, "", 0, "[L] {playerName}: {message}"),
					chat("global", 1, "", 0, "[G] {playerName}: {message}").toBuilder()
							.enabled(false)
							.build()
			);
		}
	}

	/**
	 * The only chat takes messages starting with {@code !}, and there is no fallback chat.
	 */
	static final class OnlySymbolChat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(chat("global", 0, "!", 0, "[G] {playerName}: {message}"));
			files.put(CHAT_SETTINGS, Map.of("fallback", Map.of("enabled", false, "chat", "fallback")));
			files.put(CHAT_MESSAGES, Map.of("noChatMatch", NO_CHAT));
		}
	}

	/**
	 * Global chat sits in one file of {@code chat/list/}, announcement chat on {@code !} in another.
	 */
	static final class ChatsInTwoFiles implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chatsIn("global.yml", chat("global", 0, "", 0, "[G] {playerName}: {message}"));
			files.chatsIn("announcement.yml", chat("announcement", 1, "!", 0, "[A] {playerName}: {message}"));
		}
	}
}
