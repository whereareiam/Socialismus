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
 * Two backends with SocialismusNetwork share chat through SocialismusRedis and a Redis container.
 */
@Tag("sync")
class RedisNetworkTest {
	@Test
	@Socialismus(
			network = Network.BACKENDS,
			containers = Container.REDIS,
			modules = {Module.NETWORK, Module.REDIS},
			configure = {GlobalAndLocalChat.class, ThroughRedis.class}
	)
	void aGlobalMessageReachesTheOtherBackendInItsFormatAndALocalOneStays(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob").switchesTo(Network.SURVIVAL);

		alice.chat("!hello survival");
		bob.sees("[G] Alice: hello survival");
		alice.sees("[G] Alice: hello survival");

		bob.chat("!hello lobby");
		alice.sees("[G] Bob: hello lobby");

		alice.chat("only for the lobby");
		alice.sees("[L] Alice: only for the lobby");
		bob.neverSees("only for the lobby");
	}

	@Test
	@Socialismus(
			network = Network.BACKENDS,
			containers = Container.REDIS,
			modules = {Module.NETWORK, Module.REDIS},
			configure = {GlobalAndLocalChat.class, ThroughRedis.class}
	)
	void aBackendKeepsChattingWhileRedisHangsAndSharesChatAgainOnceItAnswers(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player carol = Player.joins(anvil, "Carol");
		Player bob = Player.joins(anvil, "Bob").switchesTo(Network.SURVIVAL);
		String redis = Container.REDIS.processName();

		anvil.processes().freeze(redis);
		alice.chat("!while redis hangs");
		carol.sees("[G] Alice: while redis hangs");
		bob.neverSees("while redis hangs");

		anvil.processes().thaw(redis);
		alice.chat("!redis is back");
		bob.sees("[G] Alice: redis is back");
	}
}
