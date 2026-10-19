package me.whereareiam.socialismus.module.network.common.replication;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import me.whereareiam.socialismus.module.network.api.replication.ReplicatedCache;
import me.whereareiam.socialismus.module.network.api.replication.ReplicatedChannel;
import me.whereareiam.socialismus.module.network.api.replication.ReplicationSystem;
import me.whereareiam.socialismus.module.network.api.transport.ReplicationStore;
import me.whereareiam.socialismus.module.network.api.transport.ReplicationTransport;
import me.whereareiam.socialismus.service.SerializationService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * The replication system of one node: its caches and channels, and the transport and store they replicate through.
 * Every message between nodes goes over one channel of the transport and is dispatched by its {@link Envelope}.
 *
 * <p>When a transport is attached, or reconnects, this node catches up: with a store it loads every cache from the
 * store, otherwise it asks the other nodes for their entries. Either way, only newer revisions change its copies.</p>
 */
public final class DefaultReplicationSystem implements ReplicationSystem, ReplicationContext {
	/**
	 * The transport channel the network uses.
	 */
	public static final String CHANNEL = "socialismus:network";

	/**
	 * What an envelope adds around the entries of a snapshot, with room to spare, besides the cache's name.
	 */
	private static final int ENVELOPE_OVERHEAD = 512;

	private final NodeId self;
	private final NetworkClock clock = new NetworkClock();
	private final Clock time;
	private final SerializationService serialization;
	private final Executor executor;
	private final Map<String, DefaultReplicatedCache<?>> caches = new ConcurrentHashMap<>();
	private final Map<String, DefaultReplicatedChannel<?>> channels = new ConcurrentHashMap<>();
	private final Set<ReplicationTransport> subscribed = ConcurrentHashMap.newKeySet();

	private volatile ReplicationTransport transport;
	private volatile ReplicationStore store;

	/**
	 * Creates the replication system of a node, holding the node alone until a transport is attached.
	 *
	 * @param self          the node
	 * @param time          the wall clock, for expiry
	 * @param serialization encodes values and messages
	 * @param executor      runs the store's calls and what arrives from the transport
	 */
	public DefaultReplicationSystem(
			@NotNull NodeId self,
			@NotNull Clock time,
			@NotNull SerializationService serialization,
			@NotNull Executor executor
	) {
		this.self = self;
		this.time = time;
		this.serialization = serialization;
		this.executor = executor;
	}

	@Override
	public @NotNull <T> ReplicatedCache<T> cache(@NotNull String name, @NotNull Class<T> type) {
		if (channels.containsKey(name)) throw new IllegalArgumentException(name + " is a channel, not a cache");

		DefaultReplicatedCache<?> cache = caches.get(name);
		if (cache == null) cache = create(name, type);
		if (cache.type() != type)
			throw new IllegalArgumentException("Cache " + name + " holds " + cache.type().getSimpleName() + ", not " + type.getSimpleName());

		@SuppressWarnings("unchecked")
		ReplicatedCache<T> typed = (ReplicatedCache<T>) cache;

		return typed;
	}

	private <T> DefaultReplicatedCache<?> create(String name, Class<T> type) {
		DefaultReplicatedCache<T> created = new DefaultReplicatedCache<>(name, type, this);
		DefaultReplicatedCache<?> existing = caches.putIfAbsent(name, created);
		if (existing != null) return existing;

		// A cache asked for after the node caught up catches up on its own.
		if (transport != null) executor.execute(() -> catchUp(name));

		return created;
	}

	@Override
	public @NotNull <T> ReplicatedChannel<T> channel(@NotNull String name, @NotNull Class<T> type) {
		if (caches.containsKey(name)) throw new IllegalArgumentException(name + " is a cache, not a channel");

		DefaultReplicatedChannel<?> channel = channels.computeIfAbsent(name, ignored -> new DefaultReplicatedChannel<>(name, type, this));
		if (channel.type() != type)
			throw new IllegalArgumentException("Channel " + name + " carries " + channel.type().getSimpleName() + ", not " + type.getSimpleName());

		@SuppressWarnings("unchecked")
		ReplicatedChannel<T> typed = (ReplicatedChannel<T>) channel;

		return typed;
	}

	/**
	 * Starts replicating through a transport, and through its store when it has one, and catches up with the network.
	 *
	 * @param transport the transport
	 * @param store     its store, or {@code null} when it cannot store
	 */
	public void attach(@NotNull ReplicationTransport transport, @Nullable ReplicationStore store) {
		this.transport = transport;
		this.store = store;
		if (subscribed.add(transport)) listen(transport);

		Logger.info("Replicating through %s%s", transport.name(), store == null ? "" : ", which also stores");
		executor.execute(() -> catchUp(""));
	}

	/**
	 * Receives what a transport carries and catches up whenever it reconnects, for as long as it is the attached one.
	 * A transport is listened to once, even when it is attached again.
	 */
	private void listen(ReplicationTransport listened) {
		listened.subscribe(CHANNEL, message -> {
			if (transport == listened) executor.execute(() -> receive(message));
		});
		listened.onReconnect(() -> {
			if (transport == listened) executor.execute(() -> catchUp(""));
		});
	}

	/**
	 * Stops replicating through a transport that went away; this node keeps its copies and goes on alone.
	 *
	 * @param transport the transport that went away
	 */
	public void detach(@NotNull ReplicationTransport transport) {
		if (this.transport != transport) return;

		this.transport = null;
		this.store = null;
		Logger.warn("%s went away; this node is alone on the network until another transport is registered", transport.name());
	}

	/**
	 * Returns whether a transport is attached and connected.
	 */
	public boolean isConnected() {
		ReplicationTransport current = transport;
		return current != null && current.isConnected();
	}

	/**
	 * Forgets what expired in every cache.
	 */
	public void purge() {
		caches.values().forEach(DefaultReplicatedCache::purge);
	}

	/**
	 * Catches up with the network: loads caches from the store, or asks the other nodes for their entries.
	 *
	 * @param name the cache to catch up, or empty for every cache
	 */
	void catchUp(@NotNull String name) {
		ReplicationStore current = store;
		if (current != null) {
			caches.forEach((cacheName, cache) -> {
				if (name.isEmpty() || name.equals(cacheName)) loadSafely(cache, current);
			});
			return;
		}

		send(new Envelope(self, Envelope.Kind.SNAPSHOT_REQUEST, name, new byte[0]));
	}

	/**
	 * Handles a message from the transport.
	 */
	void receive(byte @NotNull [] message) {
		Envelope envelope = Envelope.decode(message);
		if (envelope == null || envelope.getSender().equals(self)) return;

		try {
			switch (envelope.getKind()) {
				case CHANGE -> {
					DefaultReplicatedCache<?> cache = caches.get(envelope.getName());
					if (cache != null) cache.receive(EntryState.decode(envelope.getBody()));
				}
				case SNAPSHOT -> {
					DefaultReplicatedCache<?> cache = caches.get(envelope.getName());
					if (cache != null) EntryState.decodeAll(envelope.getBody()).forEach(cache::receive);
				}
				case SNAPSHOT_REQUEST -> caches.forEach((name, cache) -> {
					if (envelope.getName().isEmpty() || envelope.getName().equals(name)) sendSnapshot(name, cache.snapshot());
				});
				case MESSAGE -> {
					DefaultReplicatedChannel<?> channel = channels.get(envelope.getName());
					if (channel != null) channel.receive(envelope.getBody(), envelope.getSender());
				}
			}
		} catch (RuntimeException failure) {
			Logger.warn("Ignoring a %s message from %s: %s", envelope.getKind(), envelope.getSender(), failure.getMessage());
		}
	}

	/**
	 * Sends the entries of a cache in as many snapshots as the transport's message size needs.
	 */
	private void sendSnapshot(String name, List<EntryState> states) {
		ReplicationTransport current = transport;
		if (current == null) return;

		// A name takes up to three bytes per character as it travels.
		int budget = current.maxMessageSize() - ENVELOPE_OVERHEAD - name.length() * 3;
		List<EntryState> batch = new ArrayList<>();
		int size = 0;
		for (EntryState state : states) {
			int stateSize = EntryState.encodeAll(List.of(state)).length;
			if (!batch.isEmpty() && size + stateSize > budget) {
				send(new Envelope(self, Envelope.Kind.SNAPSHOT, name, EntryState.encodeAll(batch)));
				batch = new ArrayList<>();
				size = 0;
			}
			batch.add(state);
			size += stateSize;
		}

		if (!batch.isEmpty() || states.isEmpty())
			send(new Envelope(self, Envelope.Kind.SNAPSHOT, name, EntryState.encodeAll(batch)));
	}

	private void loadSafely(DefaultReplicatedCache<?> cache, ReplicationStore store) {
		try {
			cache.load(store);
		} catch (RuntimeException failure) {
			Logger.warn("Could not load a cache from the store: %s", failure.getMessage());
		}
	}

	@Override
	public @NotNull NodeId self() {
		return self;
	}

	@Override
	public @NotNull NetworkClock clock() {
		return clock;
	}

	@Override
	public @NotNull Clock time() {
		return time;
	}

	@Override
	public @NotNull SerializationService serialization() {
		return serialization;
	}

	@Override
	public @NotNull Executor executor() {
		return executor;
	}

	@Override
	public void send(@NotNull Envelope envelope) {
		ReplicationTransport current = transport;
		if (current == null) return;

		byte[] message = envelope.encode();
		if (message.length > current.maxMessageSize()) {
			Logger.warn("Dropping a %s message of %s: %d bytes is more than %s carries", envelope.getKind(), envelope.getName(),
					message.length, current.name());
			return;
		}

		try {
			current.publish(CHANNEL, message);
		} catch (RuntimeException failure) {
			Logger.warn("Could not send a %s message through %s: %s", envelope.getKind(), current.name(), failure.getMessage());
		}
	}

	@Override
	public @Nullable ReplicationStore store() {
		return store;
	}
}
