package me.whereareiam.socialismus.common.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.player.SocialismusPlayerAddedEvent;
import me.whereareiam.socialismus.event.player.SocialismusPlayerRemovedEvent;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.registry.PlayerSource;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Default implementation of PlayerRegistry.
 * Stores player data in InterceptPlayer instances using a thread-safe ConcurrentHashMap.
 */
@Singleton
public class DefaultPlayerRegistry implements PlayerRegistry {
	private final Map<UUID, SocialismusPlayer> playerDataMap = new ConcurrentHashMap<>();
	private final List<PlayerSource> sources = new CopyOnWriteArrayList<>();
	private final EventManager eventManager;

	@Inject
	public DefaultPlayerRegistry(@NotNull EventManager eventManager) {
		this.eventManager = eventManager;
	}

	@Override
	@NotNull
	public Optional<SocialismusPlayer> getPlayerData(@NotNull UUID playerId) {
		return Optional.ofNullable(playerDataMap.get(playerId));
	}

	@Override
	@NotNull
	public Optional<SocialismusPlayer> getPlayerData(@NotNull String username) {
		return playerDataMap.values()
				.stream()
				.filter(player -> player.getUsername().equalsIgnoreCase(username))
				.findFirst();
	}

	@Override
	public void removePlayerData(@NotNull UUID playerId) {
		SocialismusPlayer removed = playerDataMap.remove(playerId);

		if (removed != null)
			eventManager.call(new SocialismusPlayerRemovedEvent(removed));
	}

	@Override
	public boolean hasPlayerData(@NotNull UUID playerId) {
		return playerDataMap.containsKey(playerId);
	}

	@Override
	public void syncPlayerData(@NotNull SocialismusPlayer player) {
		playerDataMap.compute(player.getUniqueId(), (id, stored) -> {
			if (stored == null) {
				eventManager.call(new SocialismusPlayerAddedEvent(player));
				return player;
			}

			if (stored == player) return stored;

			// Preserve the canonical state when new wrappers are created.
			// Preserve all custom data (including LAST_CHAT, LAST_TRIGGER, etc.)
			player.setCustomData(stored.getCustomData());

			// Preserve location (backend-specific, will be no-op on proxy)
			if (stored.getLocation() != null) player.setLocation(stored.getLocation());
			
			// Preserve server: the proxy's server on a proxy, the learned name of this server behind one
			if (stored.getServer() != null) player.setServer(stored.getServer());
			
			return player;
		});
	}

	@Override
	@NotNull
	public Collection<SocialismusPlayer> getPlayers() {
		return playerDataMap.values();
	}

	@Override
	public void addSource(@NotNull PlayerSource source) {
		sources.add(source);
	}

	@Override
	public void removeSource(@NotNull PlayerSource source) {
		sources.remove(source);
	}

	@Override
	@NotNull
	public Optional<SocialismusPlayer> findPlayer(@NotNull String username) {
		Optional<SocialismusPlayer> hosted = getPlayerData(username);
		if (hosted.isPresent()) return hosted;

		return sources.stream()
				.map(source -> source.findPlayer(username))
				.flatMap(Optional::stream)
				.findFirst();
	}

	@Override
	@NotNull
	public Collection<SocialismusPlayer> getKnownPlayers() {
		Map<UUID, SocialismusPlayer> known = new LinkedHashMap<>(playerDataMap);
		sources.forEach(source -> source.knownPlayers().forEach(player -> known.putIfAbsent(player.getUniqueId(), player)));

		return List.copyOf(known.values());
	}
}

