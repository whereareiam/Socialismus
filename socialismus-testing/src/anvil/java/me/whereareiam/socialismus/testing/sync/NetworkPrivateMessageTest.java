package me.whereareiam.socialismus.testing.sync;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.Container;
import me.whereareiam.socialismus.testing.environment.Module;
import me.whereareiam.socialismus.testing.environment.Network;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Dialogue's private messages between players on different backends, through SocialismusNetwork.
 */
@Tag("sync")
class NetworkPrivateMessageTest {
	@Test
	@Socialismus(
			network = Network.BACKENDS,
			containers = Container.REDIS,
			modules = {Module.NETWORK, Module.REDIS, Module.DIALOGUE},
			configure = ThroughRedis.class
	)
	void aPrivateMessageReachesAPlayerOnAnotherBackendAndIsConfirmedToItsSender(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob").switchesTo(Network.SURVIVAL);

		alice.command("message Bob hello from the lobby");
		bob.sees("Alice -> Bob: hello from the lobby");
		alice.sees("Alice -> Bob: hello from the lobby");

		bob.command("reply hello from survival");
		alice.sees("Bob -> Alice: hello from survival");
	}
}
