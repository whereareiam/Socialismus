package me.whereareiam.socialismus.testing.sync;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.Module;
import me.whereareiam.socialismus.testing.environment.Network;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Two backends with SocialismusNetwork share chat through SocialismusChannelizer, as plugin messages the proxy
 * forwards through the players on them.
 */
@Tag("sync")
class ChannelizerNetworkTest {
	@Test
	@Socialismus(
			network = Network.BACKENDS,
			modules = {Module.NETWORK, Module.CHANNELIZER},
			configure = GlobalAndLocalChat.class
	)
	void aGlobalMessageReachesTheOtherBackendAndALocalOneStays(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob").switchesTo(Network.SURVIVAL);

		alice.chat("!hello survival");
		bob.sees("[G] Alice: hello survival");

		bob.chat("!hello lobby");
		alice.sees("[G] Bob: hello lobby");

		alice.chat("only for the lobby");
		alice.sees("[L] Alice: only for the lobby");
		bob.neverSees("only for the lobby");
	}
}
