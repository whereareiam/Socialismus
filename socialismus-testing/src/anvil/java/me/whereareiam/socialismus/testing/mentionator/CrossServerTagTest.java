package me.whereareiam.socialismus.testing.mentionator;

import me.whereareiam.anvil.api.scenario.ScenarioContext;
import me.whereareiam.socialismus.testing.environment.Container;
import me.whereareiam.socialismus.testing.environment.Module;
import me.whereareiam.socialismus.testing.environment.Network;
import me.whereareiam.socialismus.testing.environment.ServerFiles;
import me.whereareiam.socialismus.testing.environment.Socialismus;
import me.whereareiam.socialismus.testing.journey.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

/**
 * A tag across the backends of a network, through SocialismusNetwork over Redis: the server of the tagged player
 * notifies them.
 */
@Tag("mentionator")
class CrossServerTagTest {
	@Test
	@Socialismus(
			network = Network.BACKENDS,
			containers = Container.REDIS,
			modules = {Module.NETWORK, Module.REDIS, Module.MENTIONATOR},
			configure = GlobalChatThroughRedis.class
	)
	void aPlayerOnAnotherBackendIsTaggedAndNotifiedThere(ScenarioContext anvil) {
		Player alice = Player.joins(anvil, "Alice");
		Player bob = Player.joins(anvil, "Bob").switchesTo(Network.SURVIVAL);

		alice.chat("@Bob, over here");

		bob.sees("[G] Alice: @Bob, over here");
		bob.seesActionBar("Alice tagged you in global chat");
		bob.hears("minecraft:block.note_block.pling");
	}

	/**
	 * Global chat on every backend, and SocialismusRedis at the Redis container of the network.
	 */
	static final class GlobalChatThroughRedis implements Consumer<ServerFiles> {
		@Override
		public void accept(ServerFiles files) {
			MentionatorFiles.globalChat(files);
			files.text("modules/socialismus-redis/settings.yml", """
					redis:
					  host: "${anvil.process.redis.host}"
					  port: ${anvil.process.redis.port}
					  password: ""
					  ssl: false
					  timeout: 2000
					""");
		}
	}
}
