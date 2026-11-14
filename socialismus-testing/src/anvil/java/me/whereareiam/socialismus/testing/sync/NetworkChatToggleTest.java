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
 * A chat a player turned off follows them to the other backends, through SocialismusNetwork over Redis.
 */
@Tag("sync")
class NetworkChatToggleTest {
	@Test
	@Socialismus(
			network = Network.BACKENDS,
			containers = Container.REDIS,
			modules = {Module.NETWORK, Module.REDIS},
			configure = {GlobalAndLocalChat.class, ThroughRedis.class}
	)
	void aChatTurnedOffOnOneBackendStaysOffOnAnother(ScenarioContext anvil) {
		Player bob = Player.joins(anvil, "Bob").switchesTo(Network.SURVIVAL);
		Player alice = Player.joins(anvil, "Alice");

		alice.command("socialismus toggle global");
		alice.sees("You no longer see global chat");
		alice.switchesTo(Network.SURVIVAL);

		bob.chat("!anyone on survival");
		bob.sees("[G] Bob: anyone on survival");
		alice.neverSees("anyone on survival");

		alice.command("socialismus toggle global");
		alice.sees("You see global chat again");
		bob.chat("!welcome back");
		alice.sees("[G] Bob: welcome back");
	}
}
