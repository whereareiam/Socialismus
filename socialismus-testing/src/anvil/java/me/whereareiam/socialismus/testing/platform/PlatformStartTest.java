package me.whereareiam.socialismus.testing.platform;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.MinecraftVersions;
import me.whereareiam.socialismus.testing.environment.Network;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Console;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static me.whereareiam.socialismus.testing.environment.ServerFiles.chat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every jar Socialismus ships starts on the platform it is for, formats chat there and, for the BUKKIT jar, replies
 * to commands.
 */
@Tag("platform")
@Tag("compatibility")
class PlatformStartTest {
	@Test
	@MinecraftVersions(since = "1.20.6")
	@Socialismus(network = Network.PAPER, configure = GlobalChat.class)
	void paperJarRunsOnPaper(ScenarioContext anvil) {
		startsAndChats(anvil, "Platform: PAPER [PAPER]");
	}

	// Below 1.20.6 a Paper network installs the BUKKIT jar, as the PAPER jar needs the Paper API of 1.20.6.
	@Test
	@MinecraftVersions(before = "1.20.6")
	@Socialismus(network = Network.PAPER, configure = GlobalChat.class)
	void bukkitJarRunsOnPaper(ScenarioContext anvil) {
		startsAndChats(anvil, "Platform: PAPER [BUKKIT]");
		repliesToCommands(anvil, "Server platform: PAPER", "Plugin platform: BUKKIT");
	}

	@Test
	@Socialismus(network = Network.SPIGOT, configure = GlobalChat.class)
	void bukkitJarRunsOnSpigot(ScenarioContext anvil) {
		startsAndChats(anvil, "Platform: SPIGOT [BUKKIT]");
		repliesToCommands(anvil, "Server platform: SPIGOT", "Plugin platform: BUKKIT");
	}

	@Test
	@Socialismus(network = Network.VELOCITY, configure = GlobalChat.class)
	void velocityJarRunsOnVelocity(ScenarioContext anvil) {
		startsAndChats(anvil, "Platform: VELOCITY [VELOCITY]");
	}

	@Test
	@MinecraftVersions(since = "1.20.6")
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

	/**
	 * A command reply reaches the player outside the chat event, through the jar's own way of sending messages.
	 */
	private static void repliesToCommands(ScenarioContext anvil, String... reply) {
		Player carol = Player.joins(anvil, "Carol").op();
		carol.command("socialismus debug");
		carol.seesAll(reply);
	}

	static final class GlobalChat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(chat("global", 0, "", 0, "[G] {playerName}: {message}"));
		}
	}
}
