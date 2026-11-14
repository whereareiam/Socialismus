package me.whereareiam.socialismus.testing.mentionator;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.Module;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

/**
 * {@code /socialismus mentions}: a player chooses which tags notify them; a silenced tag is still shown.
 */
@Tag("mentionator")
class PlayerModesTest {
	@Test
	@Socialismus(modules = Module.MENTIONATOR, configure = ChatNotifications.class)
	void aPlayerChoosesWhichTagsNotifyThem(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");
		Player carol = Player.joins(anvil, "Carol").op();

		bob.command("socialismus mentions");
		bob.sees("You hear every tag. Tags you may use:");

		bob.command("socialismus mentions direct");
		bob.sees("You hear only tags of your name now.");
		carol.chat("@everyone listen");
		bob.sees("[G] Carol: @everyone listen");
		bob.neverSees("EVERYONE from Carol");
		alice.chat("@Bob are you there");
		bob.sees("PING from Alice");

		bob.command("socialismus mentions none");
		bob.sees("You hear no tags now.");
		alice.chat("@Bob hello again");
		bob.sees("[G] Alice: @Bob hello again");
		bob.neverSees("PING from Alice");

		bob.command("socialismus mentions all");
		bob.sees("You hear every tag now.");
		alice.chat("@Bob last time");
		bob.sees("PING from Alice");
	}

	static final class ChatNotifications implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			MentionatorFiles.globalChat(files);
			MentionatorFiles.chatNotifications(files);
			MentionatorFiles.withoutRepeatLimits(files);
		}
	}
}
