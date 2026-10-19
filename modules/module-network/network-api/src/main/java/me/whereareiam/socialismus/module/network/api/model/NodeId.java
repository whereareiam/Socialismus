package me.whereareiam.socialismus.module.network.api.model;

import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

/**
 * The identity of one Socialismus on the network: a server or a proxy running SocialismusNetwork. It is unique on
 * the network and stays the same across restarts, so what a node held before a restart is still recognised as its.
 */
@EqualsAndHashCode
public final class NodeId implements Comparable<NodeId> {
	private final String value;

	private NodeId(String value) {
		this.value = value;
	}

	/**
	 * Returns the node with the given identity.
	 *
	 * @param value the identity, such as {@code lobby-1}; neither blank nor longer than 64 characters
	 * @return the node id
	 * @throws IllegalArgumentException when the value is blank or too long
	 */
	public static @NotNull NodeId of(@NotNull String value) {
		String trimmed = value.trim();
		if (trimmed.isEmpty()) throw new IllegalArgumentException("A node id cannot be blank");
		if (trimmed.length() > 64) throw new IllegalArgumentException("A node id has at most 64 characters: " + trimmed);

		return new NodeId(trimmed);
	}

	/**
	 * Returns the identity as text.
	 *
	 * @return the identity as text
	 */
	public @NotNull String value() {
		return value;
	}

	@Override
	public int compareTo(@NotNull NodeId other) {
		return value.compareTo(other.value);
	}

	@Override
	public String toString() {
		return value;
	}
}
