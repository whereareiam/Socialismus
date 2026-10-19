package me.whereareiam.socialismus.testing.sync;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.Module;
import me.whereareiam.socialismus.testing.environment.Network;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Console;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * A proxy and its servers share chat over SocialismusLink, the proxy listening on a port of its own, without Redis.
 */
@Tag("sync")
class LinkNetworkTest {
	private static final Duration CONNECTING = Duration.ofSeconds(30);

	@Test
	@Socialismus(
			network = Network.PROXY_AND_BACKENDS,
			modules = {Module.NETWORK, Module.LINK},
			configure = {GlobalAndLocalChat.class, ThroughLink.class},
			proxyListeners = "link"
	)
	void aProxyAndItsServersShareChatOverTheLink(ScenarioContext anvil) throws InterruptedException {
		awaitServersOnTheLink(anvil, 2);
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob").switchesTo(Network.SURVIVAL);

		alice.chat("!over the link");
		bob.sees("[G] Alice: over the link");
		alice.sees("[G] Alice: over the link");
		bob.neverSees("over the link");

		bob.chat("only for survival");
		bob.sees("[L] Bob: only for survival");
		alice.neverSees("only for survival");
	}

	/**
	 * Waits until the given number of servers joined the proxy's link, which they reach once the proxy listens.
	 */
	private static void awaitServersOnTheLink(ScenarioContext anvil, int servers) throws InterruptedException {
		long deadline = System.nanoTime() + CONNECTING.toNanos();
		while (count(Console.of(anvil, Network.PROXY).log(), "joined the link") < servers) {
			if (System.nanoTime() > deadline) fail(servers + " servers did not join the link within " + CONNECTING);
			Thread.sleep(250);
		}
	}

	private static int count(String text, String part) {
		int count = 0;
		for (int index = text.indexOf(part); index >= 0; index = text.indexOf(part, index + 1)) count++;
		return count;
	}

	/**
	 * Every node shares one secret; the proxy listens on the port Anvil reserved for its link, where the servers
	 * reach it.
	 */
	static final class ThroughLink implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			files.text("modules/SocialismusLink/settings.yml", """
					secret: "6a6f75726e65792d6c696e6b2d7365637265742d303031"
					listen:
					  host: "${anvil.process.proxy.host}"
					  port: ${anvil.process.proxy.listener.link}
					proxy:
					  host: "${anvil.process.proxy.host}"
					  port: ${anvil.process.proxy.listener.link}
					""");
		}
	}
}
