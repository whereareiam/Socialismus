package me.whereareiam.socialismus.module.network.common;

import me.whereareiam.socialismus.module.network.api.NetworkService;
import me.whereareiam.socialismus.module.network.api.lease.Leases;
import me.whereareiam.socialismus.module.network.api.model.NetworkNode;
import me.whereareiam.socialismus.module.network.api.replication.ReplicationSystem;
import me.whereareiam.socialismus.module.network.api.transport.ReplicationStore;
import me.whereareiam.socialismus.module.network.api.transport.ReplicationTransport;
import me.whereareiam.socialismus.module.network.common.lease.DefaultLeases;
import me.whereareiam.socialismus.module.network.common.lease.LeaseRecord;
import me.whereareiam.socialismus.module.network.common.node.NodeDirectory;
import me.whereareiam.socialismus.module.network.common.node.NodeRecord;
import me.whereareiam.socialismus.module.network.common.replication.DefaultReplicationSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This node's part in the network: its replication system, its announcement in the directory of nodes, and its
 * leases. {@link #maintain()} keeps it alive and is called a few times per lease lifetime.
 */
public final class DefaultNetworkService implements NetworkService {
	private final DefaultReplicationSystem replication;
	private final NodeDirectory directory;
	private final Duration lifetime;
	private final Map<String, DefaultLeases> leases = new ConcurrentHashMap<>();

	/**
	 * Creates this node's part in the network.
	 *
	 * @param replication the node's replication system
	 * @param self        what the node announces
	 * @param lifetime    how long an announcement or a lease lives without being renewed
	 */
	public DefaultNetworkService(@NotNull DefaultReplicationSystem replication, @NotNull NetworkNode self, @NotNull Duration lifetime) {
		this.replication = replication;
		this.lifetime = lifetime;
		this.directory = new NodeDirectory(replication.cache(NodeDirectory.CACHE, NodeRecord.class), self, lifetime);
	}

	@Override
	public @NotNull NetworkNode self() {
		return directory.self();
	}

	@Override
	public @NotNull Collection<NetworkNode> nodes() {
		return directory.nodes();
	}

	@Override
	public boolean isConnected() {
		return replication.isConnected();
	}

	@Override
	public @NotNull ReplicationSystem replication() {
		return replication;
	}

	@Override
	public @NotNull Leases leases(@NotNull String name) {
		return leases.computeIfAbsent(name, ignored -> new DefaultLeases(
				name,
				directory.self().getId(),
				replication.cache("lease:" + name, LeaseRecord.class),
				lifetime
		));
	}

	/**
	 * Returns the directory of nodes, to change what this node announces.
	 */
	public @NotNull NodeDirectory directory() {
		return directory;
	}

	/**
	 * Starts replicating through a transport, and announces this node to the network.
	 */
	public void attach(@NotNull ReplicationTransport transport, @Nullable ReplicationStore store) {
		replication.attach(transport, store);
		directory.announce();
	}

	/**
	 * Stops replicating through a transport that went away.
	 */
	public void detach(@NotNull ReplicationTransport transport) {
		replication.detach(transport);
	}

	/**
	 * Renews this node's announcement and leases, and forgets what expired.
	 */
	public void maintain() {
		directory.announce();
		leases.values().forEach(DefaultLeases::renew);
		replication.purge();
	}

	/**
	 * Gives up this node's leases and leaves the directory, so the others take over right away.
	 */
	public void leave() {
		leases.values().forEach(DefaultLeases::releaseAll);
		directory.leave();
	}
}
