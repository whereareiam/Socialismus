package me.whereareiam.socialismus.module.network.api.player;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * The players the other nodes of the network host, as each node announces its own. A player a proxy and a server both
 * host appears once, as the server announces them, since the server knows more about them.
 */
public interface NetworkPlayers {
	/**
	 * Returns every player the other nodes host.
	 */
	@NotNull Collection<NetworkPlayer> players();

	/**
	 * Finds a player another node hosts by their unique id.
	 */
	@NotNull Optional<NetworkPlayer> find(@NotNull UUID uniqueId);

	/**
	 * Finds a player another node hosts by name, ignoring case.
	 */
	@NotNull Optional<NetworkPlayer> find(@NotNull String username);
}
