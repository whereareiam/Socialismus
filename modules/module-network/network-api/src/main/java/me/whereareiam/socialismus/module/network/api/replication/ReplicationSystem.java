package me.whereareiam.socialismus.module.network.api.replication;

import org.jetbrains.annotations.NotNull;

/**
 * Creates the caches and channels nodes share. A name identifies a cache or channel on the whole network, so every
 * node that uses it must ask for it by the same name and type; prefix it with the module, such as
 * {@code dialogue:spies}. Asking twice for one name returns the same cache or channel.
 *
 * <p>Values travel through Socialismus's serialization, as JSON, so a value type needs what it takes to be read back:
 * fields that are serializable and a constructor or builder the serializer can use.</p>
 */
public interface ReplicationSystem {
	/**
	 * Returns the replicated cache with the given name.
	 *
	 * @param name the cache's name on the network
	 * @param type the type of its values
	 * @param <T>  value type
	 * @return the cache
	 * @throws IllegalArgumentException when the name was asked for with another type, or as a channel
	 */
	@NotNull <T> ReplicatedCache<T> cache(@NotNull String name, @NotNull Class<T> type);

	/**
	 * Returns the replicated channel with the given name.
	 *
	 * @param name the channel's name on the network
	 * @param type the type of its messages
	 * @param <T>  message type
	 * @return the channel
	 * @throws IllegalArgumentException when the name was asked for with another type, or as a cache
	 */
	@NotNull <T> ReplicatedChannel<T> channel(@NotNull String name, @NotNull Class<T> type);
}
