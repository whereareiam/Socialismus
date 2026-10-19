package me.whereareiam.socialismus.module.network.api.replication;

import me.whereareiam.socialismus.module.network.api.model.Origin;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * Messages one node sends to every other node, such as a chat message that is shown on the whole network. Unlike a
 * {@link ReplicatedCache}, nothing is kept: a node that is away misses what was sent meanwhile.
 *
 * <pre>{@code
 * ReplicatedChannel<Announcement> channel = network.replication().channel("chirper:announcements", Announcement.class);
 * channel.subscribe((announcement, origin) -> show(announcement));
 * channel.publish(new Announcement("Restart in 5 minutes"));
 * }</pre>
 *
 * @param <T> message type
 */
public interface ReplicatedChannel<T> {
	/**
	 * Sends a message to every other node.
	 *
	 * @param message the message
	 */
	void publish(@NotNull T message);

	/**
	 * Receives the messages the other nodes send; a node never receives its own.
	 *
	 * @param receiver called with each message and the node it came from
	 */
	void subscribe(@NotNull BiConsumer<T, Origin> receiver);
}
