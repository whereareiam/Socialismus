package me.whereareiam.socialismus.testing.sync;

import me.whereareiam.socialismus.testing.environment.ServerFiles;

import java.util.function.Consumer;

/**
 * Points SocialismusRedis at the Redis container of the network, at the address each process reaches it.
 */
final class ThroughRedis implements Consumer<ServerFiles> {
	@Override
	public void accept(ServerFiles files) {
		files.text("modules/SocialismusRedis/settings.yml", """
				redis:
				  host: "${anvil.process.redis.host}"
				  port: ${anvil.process.redis.port}
				  password: ""
				  ssl: false
				  timeout: 2000
				""");
	}
}
