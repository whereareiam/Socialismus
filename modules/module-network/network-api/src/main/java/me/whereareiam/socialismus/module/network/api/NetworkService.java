package me.whereareiam.socialismus.module.network.api;

import me.whereareiam.socialismus.model.attribute.AttributeKey;
import me.whereareiam.socialismus.module.network.api.lease.Leases;
import me.whereareiam.socialismus.module.network.api.model.NetworkNode;
import me.whereareiam.socialismus.module.network.api.player.NetworkPlayers;
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
	 * Returns the players the other nodes host.
	 */
	@NotNull NetworkPlayers players();

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

	/**
	 * Carries an attribute of chat messages to the other nodes with every message relayed from this node, so a mark a
	 * module sets where a message is written, such as the players Mentionator tags in it, is on the message on every
	 * node. The value travels as JSON, so its type needs what {@link ReplicationSystem} asks of a value; a node reads it
	 * only while it carries the same key, and leaves it behind otherwise.
	 *
	 * <pre>{@code
	 * network.carry(Tagging.ATTRIBUTE);
	 * }</pre>
	 *
	 * @param key the attribute's key
	 */
	void carry(@NotNull AttributeKey<?> key);

	/**
	 * Stops carrying an attribute, such as when the module that carried it stops.
	 *
	 * @param key the attribute's key carried before
	 */
	void drop(@NotNull AttributeKey<?> key);
}
