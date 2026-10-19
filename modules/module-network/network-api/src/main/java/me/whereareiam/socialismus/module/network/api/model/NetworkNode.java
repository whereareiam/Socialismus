package me.whereareiam.socialismus.module.network.api.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.whereareiam.socialismus.module.network.api.type.NodeRole;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A node as it announces itself to the others, renewed for as long as it runs. A node that stops announcing itself
 * leaves the network within one lease lifetime.
 */
@Getter
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
public final class NetworkNode {
	/**
	 * The node's identity.
	 */
	private final @NotNull NodeId id;

	/**
	 * Whether the node is a server or a proxy.
	 */
	private final @NotNull NodeRole role;

	/**
	 * The name the node's server has behind a proxy, such as {@code lobby}; {@code null} for a proxy, or for a server
	 * whose name is not known yet.
	 */
	private final @Nullable String server;

	/**
	 * The version of Socialismus the node runs.
	 */
	private final @NotNull String version;
}
