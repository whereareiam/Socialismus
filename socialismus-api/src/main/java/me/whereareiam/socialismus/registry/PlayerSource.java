package me.whereareiam.socialismus.registry;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Players this server knows of but does not host, such as the players on the other servers of a network, offered to
 * the {@link PlayerRegistry} by a module. Such a player can be found by name and completed in commands; whatever
 * acts on them, such as delivering a private message, goes through the module that offered them.
 */
public interface PlayerSource {
	/**
	 * Returns every player of the source.
	 */
	@NotNull Collection<SocialismusPlayer> knownPlayers();

	/**
	 * Finds a player of the source by their unique id.
	 */
	@NotNull Optional<SocialismusPlayer> findPlayer(@NotNull UUID uniqueId);

	/**
	 * Finds a player of the source by name, ignoring case.
	 */
	@NotNull Optional<SocialismusPlayer> findPlayer(@NotNull String username);
}
