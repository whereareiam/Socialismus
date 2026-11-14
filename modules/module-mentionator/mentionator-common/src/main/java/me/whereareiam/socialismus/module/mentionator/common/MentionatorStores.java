package me.whereareiam.socialismus.module.mentionator.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.socialismus.module.mentionator.api.type.Mode;
import me.whereareiam.socialismus.module.mentionator.common.notify.ReceivedWindow;
import me.whereareiam.socialismus.module.mentionator.common.value.Values;
import me.whereareiam.socialismus.store.StoreKey;

import java.time.Instant;
import java.util.UUID;

/**
 * The stores Mentionator keeps its state in. Replicated ones are shared by every server with SocialismusNetwork, and
 * persistent ones survive restarts with SocialismusDatabase; without them each stays in this server's memory.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MentionatorStores {
	/**
	 * Which tags notify each player.
	 */
	public static final StoreKey<UUID, Mode> MODES = StoreKey.of("mentionator:modes", UUID.class, Mode.class)
			.replicated()
			.persistent();

	/**
	 * How many notifications each player got in their current window of {@code limits.received}, forgotten when it
	 * ends.
	 */
	public static final StoreKey<UUID, ReceivedWindow> RECEIVED = StoreKey.of("mentionator:received", UUID.class, ReceivedWindow.class)
			.replicated();

	/**
	 * When a sender last notified a player, by {@code sender:player}, forgotten after {@code limits.repeat}.
	 */
	public static final StoreKey<String, Instant> REPEATS = StoreKey.of("mentionator:repeats", String.class, Instant.class)
			.replicated();

	/**
	 * Each player's values of the placeholders tags name, as their own server read them.
	 */
	public static final StoreKey<UUID, Values> VALUES = StoreKey.of("mentionator:values", UUID.class, Values.class)
			.replicated();
}
