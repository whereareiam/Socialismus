package me.whereareiam.socialismus.module.network.testing;

import me.whereareiam.socialismus.module.network.api.transport.ReplicationStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Clock;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A store in memory, shared by every node of a test, whose entries expire by a clock the test controls.
 */
public final class InMemoryStore implements ReplicationStore {
	private final Clock clock;
	private final Map<String, Stored> entries = new HashMap<>();

	public InMemoryStore(@NotNull Clock clock) {
		this.clock = clock;
	}

	@Override
	public synchronized @NotNull Optional<byte[]> get(@NotNull String namespace, @NotNull String key) {
		return Optional.ofNullable(live(namespace + "\0" + key)).map(Stored::value);
	}

	@Override
	public synchronized @NotNull Map<String, byte[]> entries(@NotNull String namespace) {
		Map<String, byte[]> found = new LinkedHashMap<>();
		String prefix = namespace + "\0";
		for (String id : Map.copyOf(entries).keySet()) {
			if (!id.startsWith(prefix)) continue;

			Stored stored = live(id);
			if (stored != null) found.put(id.substring(prefix.length()), stored.value());
		}

		return found;
	}

	@Override
	public synchronized boolean compareAndPut(
			@NotNull String namespace,
			@NotNull String key,
			byte @Nullable [] expected,
			byte @NotNull [] value,
			@Nullable Duration ttl
	) {
		String id = namespace + "\0" + key;
		Stored current = live(id);
		if (expected == null ? current != null : current == null || !Arrays.equals(current.value(), expected)) return false;

		long expiresAt = ttl == null ? -1 : clock.millis() + ttl.toMillis();
		entries.put(id, new Stored(value.clone(), expiresAt));

		return true;
	}

	@Override
	public synchronized boolean compareAndRemove(@NotNull String namespace, @NotNull String key, byte @NotNull [] expected) {
		String id = namespace + "\0" + key;
		Stored current = live(id);
		if (current == null || !Arrays.equals(current.value(), expected)) return false;

		entries.remove(id);
		return true;
	}

	private Stored live(String id) {
		Stored stored = entries.get(id);
		if (stored == null) return null;
		if (stored.expiresAt() >= 0 && stored.expiresAt() <= clock.millis()) {
			entries.remove(id);
			return null;
		}

		return stored;
	}

	private record Stored(byte[] value, long expiresAt) {
	}
}
