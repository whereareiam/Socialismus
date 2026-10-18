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
 * Players turning chats off for themselves.
 */
@Tag("chat")
class ChatToggleTest {
	@Test
	@Socialismus(configure = GlobalAndStaffChats.class)
	void chatTurnedOffNoLongerReachesThePlayerButFromSendersWhoBypassIt(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");
		Player staff = Player.joins(anvil, "Staff").op();

		bob.command("socialismus toggle global");
		bob.sees("You no longer see global chat");

		alice.chat("anyone there");
		staff.sees("[G] Alice: anyone there");
		bob.neverSees("anyone there");

		staff.chat("listen up");
		bob.sees("[G] Staff: listen up");

		bob.command("socialismus toggle global");
		bob.sees("You see global chat again");
		alice.chat("welcome back");
		bob.sees("[G] Alice: welcome back");
	}

	@Test
	@Socialismus(configure = GlobalAndStaffChats.class)
	void chatThatCannotBeTurnedOffSaysSo(ScenarioContext anvil) {
		Player bob = Player.joins(anvil, "Bob");

		bob.command("socialismus toggle announcements");

		bob.sees("announcements chat cannot be turned off");
	}

	/**
	 * Global chat may be turned off; announcement chat, on {@code !}, may not.
	 */
	static final class GlobalAndStaffChats implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(
					chat("global", 0, "", 0, "[G] {playerName}: {message}"),
					chat("announcements", 1, "!", 0, "[A] {playerName}: {message}").toBuilder().toggleable(false).build()
			);
		}
	}
}
