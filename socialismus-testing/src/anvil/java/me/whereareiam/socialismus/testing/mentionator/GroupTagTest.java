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
 * The default {@code @everyone}: only senders with its permission may use it, then not again for five minutes.
 */
@Tag("mentionator")
class GroupTagTest {
	@Test
	@Socialismus(modules = Module.MENTIONATOR, configure = ChatNotifications.class)
	void everyoneIsTaggedOnlyByStaffAndNotAgainDuringItsCooldown(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");
		Player carol = Player.joins(anvil, "Carol").op();

		alice.chat("@everyone come here");
		alice.sees("Only staff may tag everyone.");
		bob.sees("[G] Alice: @everyone come here");
		bob.neverSees("EVERYONE from Alice");

		carol.chat("@everyone meeting now");
		bob.sees("EVERYONE from Carol");
		alice.sees("EVERYONE from Carol");

		carol.chat("@everyone once more");
		carol.sees("Everyone was tagged a moment ago");
		bob.sees("[G] Carol: @everyone once more");
		bob.neverSees("EVERYONE from Carol");
	}

	static final class ChatNotifications implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			MentionatorFiles.globalChat(files);
			MentionatorFiles.chatNotifications(files);
		}
	}
}
