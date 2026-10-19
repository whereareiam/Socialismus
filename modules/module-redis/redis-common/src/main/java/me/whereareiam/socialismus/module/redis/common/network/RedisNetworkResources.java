package me.whereareiam.socialismus.module.redis.common.network;

import me.whereareiam.socialismus.model.resource.ProvidedResource;
import me.whereareiam.socialismus.module.network.api.NetworkResources;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.JedisPool;

import java.util.List;

/**
 * SocialismusRedis as SocialismusNetwork's transport and store.
 */
public final class RedisNetworkResources implements AutoCloseable {
	private final RedisReplicationTransport transport;
	private final RedisReplicationStore store;

	public RedisNetworkResources(@NotNull JedisPool pool) {
		this.transport = new RedisReplicationTransport(pool);
		this.store = new RedisReplicationStore(pool);
	}

	/**
	 * Returns the resources SocialismusNetwork replicates through.
	 */
	public @NotNull List<ProvidedResource<?>> provide() {
		// The store first, so the network finds it when the transport's registration attaches it.
		return List.of(
				ProvidedResource.of(NetworkResources.STORE, store),
				ProvidedResource.of(NetworkResources.TRANSPORT, transport)
		);
	}

	@Override
	public void close() {
		transport.close();
	}
}
