package me.whereareiam.socialismus.module.dialogue.common.control;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.module.dialogue.api.MessageToggleStore;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps who turned private messages off for as long as the server runs.
 */
@Singleton
public class InMemoryMessageToggleStore implements MessageToggleStore {
	private final Set<UUID> off = ConcurrentHashMap.newKeySet();

	@Override
	public boolean isOff(@NotNull UUID player) {
		return off.contains(player);
	}

	@Override
	public void setOff(@NotNull UUID player, boolean off) {
		if (off) this.off.add(player);
		else this.off.remove(player);
	}
}
