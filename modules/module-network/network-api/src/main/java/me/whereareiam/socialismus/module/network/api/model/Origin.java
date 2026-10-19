package me.whereareiam.socialismus.module.network.api.model;

import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Where something replicated came from: this node, or another one on the network.
 *
 * <pre>{@code
 * Origin origin = message.getAttribute(NetworkAttributes.ORIGIN).orElse(Origin.local());
 * if (origin.isLocal()) showBubble(message);
 * }</pre>
 */
@EqualsAndHashCode
public final class Origin {
	private static final Origin LOCAL = new Origin(null);

	private final @Nullable NodeId node;

	private Origin(@Nullable NodeId node) {
		this.node = node;
	}

	/**
	 * Returns the origin of what happened on this node.
	 */
	public static @NotNull Origin local() {
		return LOCAL;
	}

	/**
	 * Returns the origin of what another node sent.
	 *
	 * @param node the node it came from
	 */
	public static @NotNull Origin remote(@NotNull NodeId node) {
		return new Origin(node);
	}

	/**
	 * Returns whether it happened on this node.
	 */
	public boolean isLocal() {
		return node == null;
	}

	/**
	 * Returns the node it came from; {@code null} when it happened on this node.
	 */
	public @Nullable NodeId getNode() {
		return node;
	}

	@Override
	public String toString() {
		return node == null ? "local" : "remote:" + node;
	}
}
