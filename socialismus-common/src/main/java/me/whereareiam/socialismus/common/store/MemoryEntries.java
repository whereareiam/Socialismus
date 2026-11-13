package me.whereareiam.socialismus.common.store;

import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.store.Store;
import me.whereareiam.socialismus.store.StoreReplication;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * The entries of a store as this server alone keeps them: what a store holds while nothing shares its values. A
 * value that expired is forgotten when it is next read.
 *
 * @param <V> value type
 */
@RequiredArgsConstructor
final class MemoryEntries<V> implements StoreReplication.Entries<V> {
	private final Clock clock;
	private final Map<String, Entry<V>> values = new ConcurrentHashMap<>();
	private final List<Store.ChangeListener<String, V>> listeners = new CopyOnWriteArrayList<>();

	@Override
	public @NotNull Optional<V> get(@NotNull String key) {
		Entry<V> entry = values.get(key);
		if (entry == null) return Optional.empty();
		if (!entry.expiredAt(clock.instant())) return Optional.of(entry.value());

		forget(key, entry);

		return Optional.empty();
	}

	@Override
	public @NotNull Map<String, V> entries() {
		Instant now = clock.instant();
		values.forEach((key, entry) -> {
			if (entry.expiredAt(now)) forget(key, entry);
		});

		return values.entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, each -> each.getValue().value()));
	}

	@Override
	public void put(@NotNull String key, @NotNull V value, @Nullable Duration expiry) {
		Instant expiresAt = expiry == null ? null : clock.instant().plus(expiry);
		Entry<V> previous = values.put(key, new Entry<>(value, expiresAt));

		notify(key, previous == null ? null : previous.value(), value);
	}

	@Override
	public void remove(@NotNull String key) {
		Entry<V> previous = values.remove(key);
		if (previous != null) notify(key, previous.value(), null);
	}

	@Override
	public void onChange(@NotNull Store.ChangeListener<String, V> listener) {
		listeners.add(listener);
	}

	private void forget(String key, Entry<V> entry) {
		if (values.remove(key, entry)) notify(key, entry.value(), null);
	}

	private void notify(String key, @Nullable V previous, @Nullable V current) {
		listeners.forEach(listener -> listener.changed(key, previous, current));
	}

	private record Entry<V>(@NotNull V value, @Nullable Instant expiresAt) {
		boolean expiredAt(Instant now) {
			return expiresAt != null && !expiresAt.isAfter(now);
		}
	}
}
