package me.whereareiam.socialismus.common.store;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.service.SerializationService;
import me.whereareiam.socialismus.store.Store;
import me.whereareiam.socialismus.store.StoreKey;
import me.whereareiam.socialismus.store.StorePersistence;
import me.whereareiam.socialismus.store.StoreReplication;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A store: its working copy, in memory or shared through a replication, and the persistence that saves it. Its
 * listeners hear only the working copy in use, so a copy it moved away from falls silent.
 *
 * @param <K> key type
 * @param <V> value type
 */
final class DefaultStore<K, V> implements Store<K, V> {
	private final StoreKey<K, V> key;
	private final KeyText<K> keys;
	private final SerializationService serialization;
	private final Clock clock;
	private final List<ChangeListener<K, V>> listeners = new CopyOnWriteArrayList<>();

	private volatile StoreReplication.Entries<V> entries;
	private volatile boolean replicating;
	private volatile @Nullable StorePersistence persistence;

	DefaultStore(StoreKey<K, V> key, SerializationService serialization, Clock clock) {
		this.key = key;
		this.keys = new KeyText<>(key.getKeyType(), serialization);
		this.serialization = serialization;
		this.clock = clock;
		this.entries = watched(new MemoryEntries<>(clock));
	}

	@Override
	public @NotNull StoreKey<K, V> key() {
		return key;
	}

	@Override
	public @NotNull Optional<V> get(@NotNull K key) {
		return entries.get(keys.text(key));
	}

	@Override
	public @NotNull Map<K, V> entries() {
		Map<K, V> values = new LinkedHashMap<>();
		entries.entries().forEach((text, value) -> values.put(keys.parse(text), value));

		return Map.copyOf(values);
	}

	@Override
	public void put(@NotNull K key, @NotNull V value, @Nullable Duration expiry) {
		String text = keys.text(key);
		entries.put(text, value, expiry);

		StorePersistence saving = persistence;
		if (saving == null) return;

		Instant expiresAt = expiry == null ? null : clock.instant().plus(expiry);
		saving.save(this.key.getName(), text, new StorePersistence.Saved(json(value), expiresAt))
				.exceptionally(failure -> failed("save", failure));
	}

	@Override
	public void remove(@NotNull K key) {
		String text = keys.text(key);
		entries.remove(text);

		StorePersistence saving = persistence;
		if (saving != null) saving.delete(this.key.getName(), text).exceptionally(failure -> failed("delete", failure));
	}

	@Override
	public void onChange(@NotNull ChangeListener<K, V> listener) {
		listeners.add(listener);
	}

	/**
	 * Shares the values through a replication, or keeps them in memory again; a store that is not replicated stays
	 * as it is. The values move over with the store's default expiry, and those the new copy already has stay.
	 *
	 * @param replication the replication, {@code null} for none
	 */
	synchronized void replicateWith(@Nullable StoreReplication replication) {
		if (!key.isReplicated()) return;
		if (replication == null && !replicating) return;

		StoreReplication.Entries<V> next = replication == null
				? new MemoryEntries<>(clock)
				: replication.share(key.getName(), key.getValueType());
		moveTo(watched(next));
		replicating = replication != null;
	}

	/**
	 * Saves the values through a persistence, after loading what it holds; a store that is not persistent stays as
	 * it is. A loaded value whose key this server already has does not replace it.
	 *
	 * @param persistence the persistence, {@code null} for none
	 */
	synchronized void persistWith(@Nullable StorePersistence persistence) {
		if (!key.isPersistent()) return;

		this.persistence = persistence;
		if (persistence == null) return;

		persistence.load(key.getName())
				.thenAccept(this::fill)
				.exceptionally(failure -> failed("load", failure));
	}

	private void moveTo(StoreReplication.Entries<V> next) {
		Map<String, V> carried = entries.entries();
		Duration expiry = key.getExpiry().orElse(null);
		carried.forEach((text, value) -> {
			if (next.get(text).isEmpty()) next.put(text, value, expiry);
		});

		entries = next;
	}

	private void fill(Map<String, StorePersistence.Saved> loaded) {
		Instant now = clock.instant();
		loaded.forEach((text, saved) -> {
			if (saved.expiredAt(now) || entries.get(text).isPresent()) return;

			Duration remaining = saved.expiresAt() == null ? null : Duration.between(now, saved.expiresAt());
			entries.put(text, value(saved.value()), remaining);
		});
	}

	private StoreReplication.Entries<V> watched(StoreReplication.Entries<V> copy) {
		copy.onChange((text, previous, current) -> {
			if (entries != copy) return;

			K changed = keys.parse(text);
			listeners.forEach(listener -> listener.changed(changed, previous, current));
		});

		return copy;
	}

	private String json(V value) {
		return new String(serialization.serialize(value), StandardCharsets.UTF_8);
	}

	private V value(String json) {
		return serialization.deserialize(json.getBytes(StandardCharsets.UTF_8), key.getValueType());
	}

	private <T> T failed(String action, Throwable failure) {
		Logger.severe("Could not %s a value of the store %s: %s", action, key.getName(), failure.getMessage());
		return null;
	}
}
