package me.whereareiam.socialismus.module.mentionator.common.notify;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * What a tagged player's server needs to notify them of one message: who tagged them, where, and with which tags. The
 * server the message was written on decided whom it tags; the tagged player's server only decides whether they hear
 * it.
 *
 * @param sender           the unique id of the player who tagged them
 * @param senderName       their name
 * @param senderServer     the server they wrote on, as the network names it
 * @param chat             the chat they wrote in
 * @param message          the message, as plain text
 * @param bypassesReceived whether the sender meets {@code limits.received.bypass}
 * @param pings            the tags that tag the player, in the order they were written
 */
public record TagDelivery(
		@NotNull UUID sender,
		@NotNull String senderName,
		@Nullable String senderServer,
		@Nullable String chat,
		@NotNull String message,
		boolean bypassesReceived,
		@NotNull List<Ping> pings
) {
	public TagDelivery {
		pings = List.copyOf(pings);
	}
}
