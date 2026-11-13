package me.whereareiam.socialismus.common.store;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.service.SerializationService;
import me.whereareiam.socialismus.store.Store;
import me.whereareiam.socialismus.store.StoreKey;
import me.whereareiam.socialismus.store.StorePersistence;
import me.whereareiam.socialismus.store.StoreReplication;
import me.whereareiam.socialismus.store.Stores;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The stores of Socialismus and its modules, by name, and the replication and persistence modules offer for them.
 * A store obtained later starts with what is offered at that moment.
 */
@Singleton
public class DefaultStores implements Stores {
	private final SerializationService serialization;
	private final Clock clock;
	private final Map<String, DefaultStore<?, ?>> stores = new ConcurrentHashMap<>();

	private @Nullable StoreReplication replication;
	private @Nullable StorePersistence persistence;

	@Inject
	public DefaultStores(@NotNull SerializationService serialization) {
		this(serialization, Clock.systemUTC());
	}

	DefaultStores(SerializationService serialization, Clock clock) {
		this.serialization = serialization;
		this.clock = clock;
	}

	@Override
	@SuppressWarnings("unchecked")
	public synchronized <K, V> @NotNull Store<K, V> of(@NotNull StoreKey<K, V> key) {
		DefaultStore<?, ?> existing = stores.get(key.getName());
		if (existing != null && !existing.key().equals(key))
			throw new IllegalArgumentException("The store " + key.getName() + " was obtained as " + existing.key() + ", not as " + key);
		if (existing != null) return (Store<K, V>) existing;

		DefaultStore<K, V> store = new DefaultStore<>(key, serialization, clock);
		store.replicateWith(replication);
		store.persistWith(persistence);
		stores.put(key.getName(), store);

		return store;
	}

	@Override
	public synchronized void addReplication(@NotNull StoreReplication replication) {
		this.replication = replication;
		stores.values().forEach(store -> store.replicateWith(replication));
	}

	@Override
	public synchronized void removeReplication(@NotNull StoreReplication replication) {
		if (this.replication != replication) return;

		this.replication = null;
		stores.values().forEach(store -> store.replicateWith(null));
	}

	@Override
	public synchronized void addPersistence(@NotNull StorePersistence persistence) {
		this.persistence = persistence;
		stores.values().forEach(store -> store.persistWith(persistence));
	}

	@Override
	public synchronized void removePersistence(@NotNull StorePersistence persistence) {
		if (this.persistence != persistence) return;

		this.persistence = null;
		stores.values().forEach(store -> store.persistWith(null));
	}
}
