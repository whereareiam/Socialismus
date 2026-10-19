package me.whereareiam.socialismus.module.network.common.lease;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.network.api.lease.Lease;
import me.whereareiam.socialismus.module.network.api.lease.Leases;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import me.whereareiam.socialismus.module.network.api.replication.ReplicatedCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Leases kept in a replicated cache: a lease is an entry that lives for one lifetime, and its holder writes it again
 * before then for as long as it holds the key. When the holder stops, the entry expires on every node and the key is
 * free. A holder that sees its key taken with a lower priority claims it back.
 */
public final class DefaultLeases implements Leases {
	private final String name;
	private final NodeId self;
	private final ReplicatedCache<LeaseRecord> records;
	private final Duration lifetime;
	private final Map<String, Integer> held = new ConcurrentHashMap<>();
	private final List<ChangeListener> listeners = new CopyOnWriteArrayList<>();

	/**
	 * Creates leases on a replicated cache.
	 *
	 * @param name     the leases' name, for log messages
	 * @param self     this node
	 * @param records  the cache that keeps them
	 * @param lifetime how long a lease lives without being renewed
	 */
	public DefaultLeases(
			@NotNull String name,
			@NotNull NodeId self,
			@NotNull ReplicatedCache<LeaseRecord> records,
			@NotNull Duration lifetime
	) {
		this.name = name;
		this.self = self;
		this.records = records;
		this.lifetime = lifetime;
		records.onChange(this::changed);
	}

	@Override
	public boolean claim(@NotNull String key, int priority) {
		Optional<LeaseRecord> current = records.get(key);
		if (current.isPresent() && !isMine(current.get()) && current.get().getPriority() >= priority) return false;

		held.put(key, priority);
		records.put(key, new LeaseRecord(self.value(), priority), lifetime);

		return true;
	}

	@Override
	public void release(@NotNull String key) {
		if (held.remove(key) == null) return;

		records.get(key).filter(this::isMine).ifPresent(mine -> records.remove(key));
	}

	@Override
	public boolean holds(@NotNull String key) {
		return held.containsKey(key) && records.get(key).filter(this::isMine).isPresent();
	}

	@Override
	public @NotNull Optional<Lease> holder(@NotNull String key) {
		return records.get(key).map(DefaultLeases::lease);
	}

	@Override
	public void onChange(@NotNull ChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * Writes every lease this node holds again, so none expires while it runs. Called a few times per lifetime.
	 */
	public void renew() {
		held.forEach((key, priority) -> {
			Optional<LeaseRecord> current = records.get(key);
			if (current.isPresent() && !isMine(current.get())) {
				held.remove(key);
				return;
			}

			records.put(key, new LeaseRecord(self.value(), priority), lifetime);
		});
	}

	/**
	 * Gives up every lease this node holds, such as when it stops.
	 */
	public void releaseAll() {
		List.copyOf(held.keySet()).forEach(this::release);
	}

	private void changed(String key, @Nullable LeaseRecord previous, @Nullable LeaseRecord current, Object origin) {
		Integer priority = held.get(key);
		if (priority != null && current != null && !isMine(current)) {
			if (current.getPriority() < priority) {
				// Two claims crossed and the lower one won by its revision; this node's claim outranks it.
				records.put(key, new LeaseRecord(self.value(), priority), lifetime);
				return;
			}

			held.remove(key);
			Logger.debug("%s of %s was taken over by %s", key, name, current.getHolder());
		}

		Lease before = previous == null ? null : lease(previous);
		Lease after = current == null ? null : lease(current);
		for (ChangeListener listener : listeners) {
			try {
				listener.changed(key, before, after);
			} catch (RuntimeException failure) {
				Logger.warn("A listener of %s failed on %s: %s", name, key, failure);
			}
		}
	}

	private boolean isMine(LeaseRecord record) {
		return self.value().equals(record.getHolder());
	}

	private static Lease lease(LeaseRecord record) {
		return Lease.builder().holder(NodeId.of(record.getHolder())).priority(record.getPriority()).build();
	}
}
