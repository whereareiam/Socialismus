package me.whereareiam.socialismus.testing.sync;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.Container;
import me.whereareiam.socialismus.testing.environment.Module;
import me.whereareiam.socialismus.testing.environment.Network;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Consumer;

import static me.whereareiam.socialismus.testing.environment.ServerFiles.chat;

/**
 * Servers whose {@code chat.owner} is {@code PROXY} leave their players' chat to the proxy in front of them, which
 * formats it in its own chats.
 */
@Tag("sync")
class ProxyChatOwnerTest {
	@Test
	@Socialismus(
			network = Network.PROXY_AND_BACKENDS,
			containers = Container.REDIS,
			modules = {Module.NETWORK, Module.REDIS},
			configure = {GlobalAndLocalChat.class, ThroughRedis.class, ProxyOwnsChat.class},
			proxyConfigure = ProxyGlobalChat.class
	)
	void serversLeaveTheirPlayersChatToTheProxyWhichShowsEachMessageOnce(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob").switchesTo(Network.SURVIVAL);

		alice.chat("!through the proxy");
		alice.sees("[P] Alice: through the proxy");
		bob.sees("[P] Alice: through the proxy");
		alice.neverSees("through the proxy");
		bob.neverSees("through the proxy");
	}

	/**
	 * The servers' SocialismusNetwork leaves chat to the proxy.
	 */
	static final class ProxyOwnsChat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.put("modules/SocialismusNetwork/settings.yml", Map.of("chat", Map.of(
					"enabled", true,
					"owner", "PROXY",
					"preserveFormat", true,
					"clearHistory", true
			)));
		}
	}

	/**
	 * The proxy's global chat has a format of its own, so a message the proxy handled is told apart.
	 */
	static final class ProxyGlobalChat implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.chats(
					chat("local", 0, "", 64, "[L] {playerName}: {message}"),
					chat("global", 1, "!", 0, "[P] {playerName}: {message}")
			);
		}
	}
}
