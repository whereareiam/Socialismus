package me.whereareiam.socialismus.module.network.common.store;

import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.network.api.replication.ReplicationSystem;
import me.whereareiam.socialismus.store.StoreReplication;
import org.jetbrains.annotations.NotNull;

/**
 * Shares Socialismus's replicated stores between the nodes, each in a replicated cache of its own named after the
 * store, such as {@code stores/mentionator:modes}, so no store meets a cache a module asked for by name.
 */
@RequiredArgsConstructor
public final class NetworkStoreReplication implements StoreReplication {
	static final String PREFIX = "stores/";

	private final ReplicationSystem replication;

	@Override
	public <V> @NotNull Entries<V> share(@NotNull String name, @NotNull Class<V> valueType) {
		return new ReplicatedEntries<>(name, replication.cache(PREFIX + name, valueType));
	}
}
