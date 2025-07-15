package me.whereareiam.socialismus.common.container;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.input.container.PlayerContainerService;
import me.whereareiam.socialismus.api.input.event.player.DummyPlayerAddedEvent;
import me.whereareiam.socialismus.api.input.event.player.DummyPlayerRemovedEvent;
import me.whereareiam.socialismus.api.input.event.player.DummyPlayerUpdatedEvent;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.util.EventUtil;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class PlayerContainer implements PlayerContainerService {
	private final ConcurrentHashMap<UUID, DummyPlayer> players = new ConcurrentHashMap<>();

	@Override
	public void addPlayer(DummyPlayer dummyPlayer) {
		EventUtil.callEvent(new DummyPlayerAddedEvent(dummyPlayer, false), () -> players.put(dummyPlayer.getUniqueId(), dummyPlayer));
	}

	@Override
	public void removePlayer(UUID uniqueId) {
		EventUtil.callEvent(new DummyPlayerRemovedEvent(players.get(uniqueId), false), () -> players.remove(uniqueId));
	}

	@Override
	public void updatePlayer(UUID uniqueId, DummyPlayer dummyPlayer) {
		EventUtil.callEvent(new DummyPlayerUpdatedEvent(dummyPlayer, players.get(uniqueId), false), () -> players.put(uniqueId, dummyPlayer));
	}

	@Override
	public boolean hasPlayer(UUID uniqueId) {
		return players.containsKey(uniqueId);
	}

	@Override
	public Optional<DummyPlayer> getPlayer(String username) {
		if (username == null || username.isEmpty()) return Optional.empty();

		return players.values().stream().filter(player -> player.getUsername().equals(username)).findFirst();
	}

	@Override
	public Optional<DummyPlayer> getPlayer(UUID uniqueId) {
		if (uniqueId == null) return Optional.empty();

		return Optional.ofNullable(players.get(uniqueId));
	}

	@Override
	public Set<DummyPlayer> getPlayers() {
		return new HashSet<>(players.values());
	}
}
