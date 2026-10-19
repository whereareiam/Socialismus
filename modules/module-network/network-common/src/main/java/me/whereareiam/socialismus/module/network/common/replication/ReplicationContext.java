package me.whereareiam.socialismus.module.network.common.replication;

import me.whereareiam.socialismus.module.network.api.model.NodeId;
import me.whereareiam.socialismus.module.network.api.transport.ReplicationStore;
import me.whereareiam.socialismus.module.network.common.wire.Envelope;
import me.whereareiam.socialismus.module.network.common.wire.NetworkClock;
import me.whereareiam.socialismus.service.SerializationService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Clock;
import java.util.concurrent.Executor;

/**
 * What a cache, channel or lease needs from the replication system around it.
 */
interface ReplicationContext {
	@NotNull NodeId self();

	@NotNull NetworkClock clock();

	/**
	 * The wall clock, for expiry; tests replace it.
	 */
	@NotNull Clock time();

	@NotNull SerializationService serialization();

	/**
	 * Runs work that may block, such as a store's calls, off the caller's thread.
	 */
	@NotNull Executor executor();

	/**
	 * Sends an envelope to the other nodes; dropped while there is no transport.
	 */
	void send(@NotNull Envelope envelope);

	/**
	 * Returns the store, or {@code null} while the transport cannot store.
	 */
	@Nullable ReplicationStore store();
}
