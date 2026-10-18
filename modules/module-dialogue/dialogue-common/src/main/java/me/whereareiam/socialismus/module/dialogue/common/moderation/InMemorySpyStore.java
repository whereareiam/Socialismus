package me.whereareiam.socialismus.module.dialogue.common.moderation;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.module.dialogue.api.SpyStore;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps who spies on private messages for as long as the server runs.
 */
@Singleton
public class InMemorySpyStore implements SpyStore {
	private final Set<UUID> spies = ConcurrentHashMap.newKeySet();

	@Override
	public boolean isSpying(@NotNull UUID player) {
		return spies.contains(player);
	}

	@Override
	public void setSpying(@NotNull UUID player, boolean spying) {
		if (spying) spies.add(player);
		else spies.remove(player);
	}
}
