package me.whereareiam.socialismus.module.network.api.model;

import lombok.Builder;
import lombok.Value;
import me.whereareiam.socialismus.module.network.api.type.NodeRole;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A node as it announces itself to the others, renewed for as long as it runs. A node that stops announcing itself
 * leaves the network within one lease lifetime.
 */
@Value
@Builder(toBuilder = true)
public class NetworkNode {
	/**
	 * The node's identity.
	 */
	@NotNull NodeId id;

	/**
	 * Whether the node is a server or a proxy.
	 */
	@NotNull NodeRole role;

	/**
	 * The name the node's server has behind a proxy, such as {@code lobby}; {@code null} for a proxy, or for a server
	 * whose name is not known yet.
	 */
	@Nullable String server;

	/**
	 * The version of Socialismus the node runs.
	 */
	@NotNull String version;
}
