package me.whereareiam.socialismus.module.network.api;

import me.whereareiam.socialismus.module.network.api.lease.Leases;
import me.whereareiam.socialismus.module.network.api.model.NetworkNode;
import me.whereareiam.socialismus.module.network.api.replication.ReplicationSystem;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * The network of the Socialismus servers and proxies that run SocialismusNetwork, as this node takes part in it.
 * A module reaches it as the resource {@link NetworkResources#NETWORK}, through an optional {@code MODULE}
 * dependency on SocialismusNetwork, and keeps working on its own when SocialismusNetwork is not installed.
 *
 * <p>Without a transport, the network holds this node alone: caches, channels and leases work, but nothing leaves
 * the node. They start replicating as soon as a transport is registered.</p>
 */
public interface NetworkService {
	/**
	 * Returns this node as it announces itself.
	 *
	 * @return this node as it announces itself
	 */
	@NotNull NetworkNode self();

	/**
	 * Returns the nodes of the network this node knows of, itself included.
	 *
	 * @return the nodes of the network this node knows of, itself included
	 */
	@NotNull Collection<NetworkNode> nodes();

	/**
	 * Returns whether this node is connected to the others through a transport.
	 *
	 * @return whether this node is connected to the others through a transport
	 */
	boolean isConnected();

	/**
	 * Returns the caches and channels nodes share.
	 *
	 * @return the caches and channels nodes share
	 */
	@NotNull ReplicationSystem replication();

	/**
	 * Returns the leases with the given name, such as {@code socialismus:chat-owners}. Asking twice for one name
	 * returns the same leases.
	 *
	 * @param name the name of the leases on the network
	 * @return the leases
	 */
	@NotNull Leases leases(@NotNull String name);
}
