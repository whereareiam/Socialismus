package me.whereareiam.socialismus.event.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.whereareiam.socialismus.event.base.SynchronousEvent;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired on a proxy once a player it hosts is connected to another server, after {@link SocialismusPlayer#getServer()}
 * names the new one.
 */
@Getter
@ToString
@RequiredArgsConstructor
public class SocialismusPlayerServerChangedEvent implements SynchronousEvent {
	/**
	 * The player, now on {@link #getServer()}.
	 */
	private final @NotNull SocialismusPlayer player;

	/**
	 * The server the player was on before, or null when they just joined the proxy.
	 */
	private final @Nullable String previous;

	/**
	 * The server the player is on now.
	 */
	private final @Nullable String server;
}
