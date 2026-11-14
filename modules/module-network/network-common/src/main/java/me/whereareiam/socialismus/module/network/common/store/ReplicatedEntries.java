package me.whereareiam.socialismus.module.network.common.store;

import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.network.api.replication.ReplicatedCache;
import me.whereareiam.socialismus.store.Store;
import me.whereareiam.socialismus.store.StoreReplication;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

/**
 * A store's entries in a replicated cache: this node reads its own copy, and a change goes out to the others in the
 * background, where a failure is logged rather than handed back to the store.
 *
 * @param <V> value type
 */
@RequiredArgsConstructor
final class ReplicatedEntries<V> implements StoreReplication.Entries<V> {
	private final String store;
	private final ReplicatedCache<V> cache;

	@Override
	public @NotNull Optional<V> get(@NotNull String key) {
		return cache.get(key);
	}

	@Override
	public @NotNull Map<String, V> entries() {
		return cache.entries();
	}

	@Override
	public void put(@NotNull String key, @NotNull V value, @Nullable Duration expiry) {
		cache.put(key, value, expiry).exceptionally(failure -> failed("share", failure));
	}

	@Override
	public void remove(@NotNull String key) {
		cache.remove(key).exceptionally(failure -> failed("remove", failure));
	}

	@Override
	public void onChange(@NotNull Store.ChangeListener<String, V> listener) {
		cache.onChange((key, previous, current, origin) -> listener.changed(key, previous, current));
	}

	private Void failed(String action, Throwable failure) {
		Logger.severe("Could not %s a value of the store %s over the network: %s", action, store, failure.getMessage());
		return null;
	}
}
