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
 * A player tags another in chat, with Mentionator's default tags and notifications.
 */
@Tag("mentionator")
class TaggingTest {
	@Test
	@Socialismus(modules = Module.MENTIONATOR, configure = GlobalChat.class)
	void aTaggedPlayerIsNotifiedAndEveryoneSeesTheTag(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");
		Player carol = Player.joins(anvil, "Carol");

		alice.chat("hi bob, look");

		carol.sees("[G] Alice: hi @Bob, look");
		bob.sees("[G] Alice: hi @Bob, look");
		bob.seesActionBar("Alice tagged you in global chat");
		bob.hears("minecraft:block.note_block.pling");
	}

	@Test
	@Socialismus(modules = Module.MENTIONATOR, configure = GlobalChat.class)
	void anEscapedTagAndTheNameOfAnOfflinePlayerStayPlain(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");

		alice.chat("write \\@Bob to tag, @Zed is away");

		bob.sees("[G] Alice: write @Bob to tag, @Zed is away");
	}

	static final class GlobalChat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			MentionatorFiles.globalChat(files);
		}
	}
}
