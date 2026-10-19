package me.whereareiam.socialismus.module.dialogue.common.store;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.module.dialogue.api.IgnoreStore;
import me.whereareiam.socialismus.module.dialogue.api.model.IgnoredPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps whom each player ignores for as long as the server runs.
 */
@Singleton
public class InMemoryIgnoreStore implements IgnoreStore {
	private final Map<UUID, Map<UUID, IgnoredPlayer>> ignored = new ConcurrentHashMap<>();

	@Override
	public boolean ignores(@NotNull UUID player, @NotNull UUID other) {
		Map<UUID, IgnoredPlayer> players = ignored.get(player);

		return players != null && players.containsKey(other);
	}

	@Override
	public @NotNull List<IgnoredPlayer> ignored(@NotNull UUID player) {
		Map<UUID, IgnoredPlayer> players = ignored.get(player);
		if (players == null) return List.of();

		synchronized (players) {
			return List.copyOf(players.values());
		}
	}

	@Override
	public boolean ignore(@NotNull UUID player, @NotNull IgnoredPlayer other) {
		Map<UUID, IgnoredPlayer> players = ignored.computeIfAbsent(player, key -> new LinkedHashMap<>());
		synchronized (players) {
			return players.putIfAbsent(other.getUniqueId(), other) == null;
		}
	}

	@Override
	public boolean unignore(@NotNull UUID player, @NotNull UUID other) {
		Map<UUID, IgnoredPlayer> players = ignored.get(player);
		if (players == null) return false;

		synchronized (players) {
			return players.remove(other) != null;
		}
	}
}
