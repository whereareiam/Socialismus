package me.whereareiam.socialismus.module.network.common.replication;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import me.whereareiam.socialismus.module.network.api.model.Origin;
import me.whereareiam.socialismus.module.network.api.replication.ReplicatedCache;
import me.whereareiam.socialismus.module.network.api.replication.Versioned;
import me.whereareiam.socialismus.module.network.api.transport.ReplicationStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A replicated cache: this node's copy of every entry, each with the revision of its last change. A change applies
 * only when its revision is newer than the copy's, on every node alike, so all copies end up with the same newest
 * value. A removal is kept for a while as a marker, so an older change arriving late cannot bring the entry back.
 *
 * @param <T> value type
 */
final class DefaultReplicatedCache<T> implements ReplicatedCache<T> {
	/**
	 * How long a removal is remembered, which must outlast any change still on its way.
	 */
	static final Duration REMOVAL_MEMORY = Duration.ofMinutes(10);

	/**
	 * How often a change is tried against a store whose entry keeps changing before it gives up.
	 */
	private static final int STORE_ATTEMPTS = 5;

	private final String name;
	private final Class<T> type;
	private final ReplicationContext context;
	private final Map<String, Entry<T>> entries = new ConcurrentHashMap<>();
	private final List<ChangeListener<T>> listeners = new CopyOnWriteArrayList<>();

	DefaultReplicatedCache(String name, Class<T> type, ReplicationContext context) {
		this.name = name;
		this.type = type;
		this.context = context;
	}

	Class<T> type() {
		return type;
	}

	@Override
	public @NotNull Optional<T> get(@NotNull String key) {
		Entry<T> entry = entries.get(key);

		return isLive(entry) ? Optional.of(entry.value()) : Optional.empty();
	}

	@Override
	public @NotNull Map<String, T> entries() {
		Map<String, T> live = new LinkedHashMap<>();
		entries.forEach((key, entry) -> {
			if (isLive(entry)) live.put(key, entry.value());
		});

		return live;
	}

	@Override
	public @NotNull CompletableFuture<Void> put(@NotNull String key, @NotNull T value, @Nullable Duration ttl) {
		EntryState state = new EntryState(key, nextRevision(), encode(value), ttlMs(ttl));
		apply(state, value, Origin.local());
		announce(state);

		ReplicationStore store = context.store();
		if (store == null) return CompletableFuture.completedFuture(null);

		return CompletableFuture.runAsync(() -> storeNewest(store, state, ttl), context.executor());
	}

	@Override
	public @NotNull CompletableFuture<Void> remove(@NotNull String key) {
		EntryState state = new EntryState(key, nextRevision(), null, -1);
		apply(state, null, Origin.local());
		announce(state);

		ReplicationStore store = context.store();
		if (store == null) return CompletableFuture.completedFuture(null);

		return CompletableFuture.runAsync(() -> removeFromStore(store, state), context.executor());
	}

	@Override
	public @NotNull Optional<Versioned<T>> read(@NotNull String key) {
		Entry<T> entry = entries.get(key);

		return isLive(entry) ? Optional.of(new Versioned<>(entry.value(), entry.revision())) : Optional.empty();
	}

	@Override
	public @NotNull CompletableFuture<Boolean> write(
			@NotNull String key,
			@Nullable Versioned<T> read,
			@NotNull T value,
			@Nullable Duration ttl
	) {
		Revision expected = read == null ? null : (Revision) read.getRevision();
		ReplicationStore store = context.store();
		if (store == null) return CompletableFuture.completedFuture(writeLocally(key, expected, value, ttl));

		return CompletableFuture.supplyAsync(() -> writeToStore(store, key, expected, value, ttl), context.executor());
	}

	@Override
	public @NotNull CompletableFuture<Boolean> remove(@NotNull String key, @NotNull Versioned<T> read) {
		Revision expected = (Revision) read.getRevision();
		ReplicationStore store = context.store();
		if (store == null) return CompletableFuture.completedFuture(removeLocally(key, expected));

		return CompletableFuture.supplyAsync(() -> removeFromStore(store, key, expected), context.executor());
	}

	@Override
	public void onChange(@NotNull ChangeListener<T> listener) {
		listeners.add(listener);
	}

	/**
	 * Applies a change another node sent.
	 */
	void receive(@NotNull EntryState state) {
		context.clock().observe(state.getRevision().getCounter());
		T value = state.isRemoval() ? null : decode(state.getValue());
		if (!state.isRemoval() && value == null) return;

		apply(state, value, originOf(state.getRevision()));
	}

	/**
	 * Returns every entry this node knows, removals included, for another node that catches up.
	 */
	@NotNull List<EntryState> snapshot() {
		long now = context.time().millis();
		List<EntryState> states = new ArrayList<>();
		entries.forEach((key, entry) -> {
			if (isExpired(entry, now)) return;

			long ttlMs = entry.expiresAt() < 0 || entry.value() == null ? -1 : entry.expiresAt() - now;
			states.add(new EntryState(key, entry.revision(), entry.encoded(), ttlMs));
		});

		return states;
	}

	/**
	 * Loads the entries of the store into this node's copy, after a store was found.
	 */
	void load(@NotNull ReplicationStore store) {
		store.entries(name).forEach((key, bytes) -> {
			EntryState stored = decodeStored(key, bytes);
			if (stored != null) receive(stored);
		});
	}

	/**
	 * Forgets entries and removals that expired, telling the listeners about the entries.
	 */
	void purge() {
		long now = context.time().millis();
		for (Map.Entry<String, Entry<T>> candidate : entries.entrySet()) {
			Entry<T> entry = candidate.getValue();
			if (!isExpired(entry, now)) continue;
			if (!entries.remove(candidate.getKey(), entry)) continue;

			if (entry.value() != null) notify(candidate.getKey(), entry.value(), null, Origin.local());
		}
	}

	private boolean writeLocally(String key, @Nullable Revision expected, T value, @Nullable Duration ttl) {
		EntryState state;
		synchronized (this) {
			if (!Objects.equals(liveRevision(key), expected)) return false;

			state = new EntryState(key, nextRevision(), encode(value), ttlMs(ttl));
			apply(state, value, Origin.local());
		}
		announce(state);

		return true;
	}

	private boolean removeLocally(String key, Revision expected) {
		EntryState state;
		synchronized (this) {
			if (!Objects.equals(liveRevision(key), expected)) return false;

			state = new EntryState(key, nextRevision(), null, -1);
			apply(state, null, Origin.local());
		}
		announce(state);

		return true;
	}

	private boolean writeToStore(ReplicationStore store, String key, @Nullable Revision expected, T value, @Nullable Duration ttl) {
		byte[] stored = store.get(name, key).orElse(null);
		if (!Objects.equals(revisionOf(key, stored), expected)) return false;

		EntryState state = new EntryState(key, nextRevision(), encode(value), ttlMs(ttl));
		if (!store.compareAndPut(name, key, stored, storedBytes(state), ttl)) return false;

		apply(state, value, Origin.local());
		announce(state);

		return true;
	}

	private boolean removeFromStore(ReplicationStore store, String key, Revision expected) {
		byte[] stored = store.get(name, key).orElse(null);
		if (stored == null || !Objects.equals(revisionOf(key, stored), expected)) return false;
		if (!store.compareAndRemove(name, key, stored)) return false;

		EntryState state = new EntryState(key, nextRevision(), null, -1);
		apply(state, null, Origin.local());
		announce(state);

		return true;
	}

	/**
	 * Writes a change to the store unless the store holds a newer one already.
	 */
	private void storeNewest(ReplicationStore store, EntryState state, @Nullable Duration ttl) {
		for (int attempt = 0; attempt < STORE_ATTEMPTS; attempt++) {
			byte[] stored = store.get(name, state.getKey()).orElse(null);
			Revision storedRevision = revisionOf(state.getKey(), stored);
			if (storedRevision != null && !state.getRevision().isNewerThan(storedRevision)) return;
			if (store.compareAndPut(name, state.getKey(), stored, storedBytes(state), ttl)) return;
		}

		Logger.warn("Could not store %s of %s: the entry kept changing", state.getKey(), name);
	}

	private void removeFromStore(ReplicationStore store, EntryState removal) {
		for (int attempt = 0; attempt < STORE_ATTEMPTS; attempt++) {
			byte[] stored = store.get(name, removal.getKey()).orElse(null);
			if (stored == null) return;

			Revision storedRevision = revisionOf(removal.getKey(), stored);
			if (storedRevision != null && !removal.getRevision().isNewerThan(storedRevision)) return;
			if (store.compareAndRemove(name, removal.getKey(), stored)) return;
		}

		Logger.warn("Could not remove %s of %s from the store: the entry kept changing", removal.getKey(), name);
	}

	/**
	 * Applies a change to this node's copy when it is newer, and tells the listeners.
	 */
	private void apply(EntryState state, @Nullable T value, Origin origin) {
		long now = context.time().millis();
		T previous;
		synchronized (this) {
			Entry<T> current = entries.get(state.getKey());
			if (current != null && !state.getRevision().isNewerThan(current.revision())) return;

			entries.put(state.getKey(), new Entry<>(state.getRevision(), value, state.getValue(), expiresAt(state, now)));
			previous = isLive(current, now) ? current.value() : null;
		}

		if (previous != null || value != null) notify(state.getKey(), previous, value, origin);
	}

	/**
	 * Returns when this node's copy of a change expires: a removal once it is remembered long enough, an entry when its
	 * lifetime ends, in epoch milliseconds, or {@code -1} never.
	 */
	private static long expiresAt(EntryState state, long now) {
		if (state.isRemoval()) return now + REMOVAL_MEMORY.toMillis();

		return state.getTtlMs() < 0 ? -1 : now + state.getTtlMs();
	}

	private void announce(EntryState state) {
		context.send(new Envelope(context.self(), Envelope.Kind.CHANGE, name, state.encode()));
	}

	private void notify(String key, @Nullable T previous, @Nullable T current, Origin origin) {
		for (ChangeListener<T> listener : listeners) {
			try {
				listener.changed(key, previous, current, origin);
			} catch (RuntimeException failure) {
				Logger.warn("A listener of %s failed on %s: %s", name, key, failure);
			}
		}
	}

	private @Nullable Revision liveRevision(String key) {
		Entry<T> entry = entries.get(key);

		return isLive(entry) ? entry.revision() : null;
	}

	private @Nullable Revision revisionOf(String key, byte @Nullable [] stored) {
		if (stored == null) return null;

		EntryState state = decodeStored(key, stored);

		return state == null ? null : state.getRevision();
	}

	private static byte[] storedBytes(EntryState state) {
		// The store keeps the expiry itself, so what it holds never carries one.
		return state.withoutExpiry().encode();
	}

	private @Nullable EntryState decodeStored(String key, byte[] bytes) {
		try {
			EntryState state = EntryState.decode(bytes);
			return state.getKey().equals(key) ? state : null;
		} catch (RuntimeException failure) {
			Logger.warn("Ignoring %s of %s in the store: %s", key, name, failure.getMessage());
			return null;
		}
	}

	private Revision nextRevision() {
		return new Revision(context.clock().tick(), context.self());
	}

	private Origin originOf(Revision revision) {
		NodeId writer = revision.getNode();

		return writer.equals(context.self()) ? Origin.local() : Origin.remote(writer);
	}

	private byte[] encode(T value) {
		return context.serialization().serialize(value);
	}

	private @Nullable T decode(byte[] bytes) {
		try {
			return context.serialization().deserialize(bytes, type);
		} catch (RuntimeException failure) {
			Logger.warn("Ignoring a value of %s that cannot be read as %s: %s", name, type.getSimpleName(), failure.getMessage());
			return null;
		}
	}

	private static long ttlMs(@Nullable Duration ttl) {
		return ttl == null ? -1 : Math.max(0, ttl.toMillis());
	}

	private boolean isLive(@Nullable Entry<T> entry) {
		return isLive(entry, context.time().millis());
	}

	private static <T> boolean isLive(@Nullable Entry<T> entry, long now) {
		return entry != null && entry.value() != null && !isExpired(entry, now);
	}

	private static boolean isExpired(Entry<?> entry, long now) {
		return entry.expiresAt() >= 0 && entry.expiresAt() <= now;
	}

	/**
	 * This node's copy of one entry.
	 *
	 * @param revision  the revision of its last change
	 * @param value     the value, or {@code null} when it was removed
	 * @param encoded   the value as it travels
	 * @param expiresAt when it expires, in epoch milliseconds, or {@code -1} never
	 */
	private record Entry<T>(Revision revision, @Nullable T value, byte @Nullable [] encoded, long expiresAt) {
	}
}
