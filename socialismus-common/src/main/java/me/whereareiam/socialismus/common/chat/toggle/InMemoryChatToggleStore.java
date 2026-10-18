package me.whereareiam.socialismus.common.chat.toggle;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.service.chat.ChatToggleStore;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps which chats each player turned off for as long as the server runs.
 */
@Singleton
public class InMemoryChatToggleStore implements ChatToggleStore {
	private final Map<UUID, Set<String>> off = new ConcurrentHashMap<>();

	@Override
	public boolean isOff(@NotNull UUID player, @NotNull String chat) {
		Set<String> chats = off.get(player);

		return chats != null && chats.contains(chat);
	}

	@Override
	public void setOff(@NotNull UUID player, @NotNull String chat, boolean off) {
		if (off) this.off.computeIfAbsent(player, key -> ConcurrentHashMap.newKeySet()).add(chat);
		else this.off.computeIfPresent(player, (key, chats) -> {
			chats.remove(chat);
			return chats.isEmpty() ? null : chats;
		});
	}
}
