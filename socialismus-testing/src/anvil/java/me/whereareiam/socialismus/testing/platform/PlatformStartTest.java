package me.whereareiam.socialismus.testing.platform;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.Network;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Console;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static me.whereareiam.socialismus.testing.environment.ServerFiles.chat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every jar Socialismus ships starts on the platform it is for and formats chat there.
 */
@Tag("platform")
class PlatformStartTest {
	@Test
	@Socialismus(network = Network.PAPER, configure = GlobalChat.class)
	void paperJarRunsOnPaper(ScenarioContext anvil) {
		startsAndChats(anvil, "Platform: PAPER [PAPER]");
	}

	@Test
	@Disabled("The BUKKIT jar does not start on Spigot yet: Attache downloads Adventure and Cloud without the "
			+ "relocation its code refers to, and cannot download Brigadier")
	@Socialismus(network = Network.SPIGOT, configure = GlobalChat.class)
	void bukkitJarRunsOnSpigot(ScenarioContext anvil) {
		startsAndChats(anvil, "Platform: BUKKIT [BUKKIT]");
	}

	@Test
	@Socialismus(network = Network.VELOCITY, configure = GlobalChat.class)
	void velocityJarRunsOnVelocity(ScenarioContext anvil) {
		startsAndChats(anvil, "Platform: VELOCITY [VELOCITY]");
	}

	@Test
	@Socialismus(network = Network.PAPER, bundle = true, configure = GlobalChat.class)
	void bundleRunsOnPaper(ScenarioContext anvil) {
		startsAndChats(anvil, "Platform: PAPER [PAPER]");
	}

	@Test
	@Socialismus(network = Network.VELOCITY, bundle = true, configure = GlobalChat.class)
	void bundleRunsOnVelocity(ScenarioContext anvil) {
		startsAndChats(anvil, "Platform: VELOCITY [VELOCITY]");
	}

	private static void startsAndChats(ScenarioContext anvil, String banner) {
		String log = Console.of(anvil).log();
		assertTrue(log.contains(banner), "The startup banner does not name the platform as " + banner);

		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob");
		alice.chat("hello");
		alice.sees("[G] Alice: hello");
		bob.sees("[G] Alice: hello");
	}

	static final class GlobalChat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(chat("global", 0, "", 0, "[G] {playerName}: {message}"));
		}
	}
}
