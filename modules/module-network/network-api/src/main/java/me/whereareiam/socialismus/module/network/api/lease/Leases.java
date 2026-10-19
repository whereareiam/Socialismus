package me.whereareiam.socialismus.module.network.api.lease;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Keys that at most one node holds at a time, such as which node handles a player's chat. A node holds a key for as
 * long as it renews it, which it does by itself while it runs; the keys of a node that stops or crashes expire within
 * one lease lifetime and can be claimed by another.
 *
 * <p>A claim takes a key that nobody holds, that its holder lets expire, or that is held with a lower priority. Of
 * two claims at the same moment the higher priority wins; between equal ones, the newer.</p>
 *
 * <pre>{@code
 * Leases owners = network.leases("chat-owners");
 * owners.claim(player.toString(), 10);
 * if (owners.holds(player.toString())) handle(message);
 * }</pre>
 */
public interface Leases {
	/**
	 * Claims a key for this node and renews it from then on, unless another node holds it with a higher priority.
	 * Claiming a key this node holds again changes its priority.
	 *
	 * @param key      the key
	 * @param priority the priority of the claim
	 * @return whether this node holds the key after the claim, as far as it knows now
	 */
	boolean claim(@NotNull String key, int priority);

	/**
	 * Gives a key up, so another node can claim it right away.
	 *
	 * @param key the key
	 */
	void release(@NotNull String key);

	/**
	 * Returns whether this node holds a key.
	 *
	 * @param key the key
	 * @return whether this node holds it
	 */
	boolean holds(@NotNull String key);

	/**
	 * Returns who holds a key.
	 *
	 * @param key the key
	 * @return the lease, or empty when nobody holds the key
	 */
	@NotNull Optional<Lease> holder(@NotNull String key);

	/**
	 * Calls a listener whenever the holder of a key changes, such as when another node takes it over.
	 *
	 * @param listener called with the key, the lease before and the lease now; either is {@code null} when nobody held
	 *                 the key
	 */
	void onChange(@NotNull ChangeListener listener);

	/**
	 * A change of who holds a key.
	 */
	@FunctionalInterface
	interface ChangeListener {
		void changed(@NotNull String key, @Nullable Lease previous, @Nullable Lease current);
	}
}
