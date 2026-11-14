package me.whereareiam.socialismus.common.chat.toggle;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.store.Store;
import me.whereareiam.socialismus.store.StoreKey;
import me.whereareiam.socialismus.store.Stores;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Which chats each player turned off for themselves, by player and chat, in a store that every server shares and
 * that outlives restarts, so a chat a player turned off stays off on every server of a network.
 */
@Singleton
public class ChatToggles {
	/**
	 * The store of the toggles: the chats each player turned off, by player and chat.
	 */
	public static final StoreKey<String, Boolean> STORE = StoreKey.of("socialismus:chat-toggles", String.class, Boolean.class)
			.replicated()
			.persistent();

	private final Store<String, Boolean> off;

	/**
	 * Keeps the toggles in Socialismus's stores.
	 *
	 * @param stores the stores
	 */
	@Inject
	public ChatToggles(@NotNull Stores stores) {
		this.off = stores.of(STORE);
	}

	/**
	 * Returns whether a player turned a chat off.
	 *
	 * @param player the player's unique id
	 * @param chat   the chat's identifier
	 * @return whether the chat is off for them
	 */
	public boolean isOff(@NotNull UUID player, @NotNull String chat) {
		return off.get(key(player, chat)).orElse(false);
	}

	/**
	 * Turns a chat off for a player, or back on.
	 *
	 * @param player the player's unique id
	 * @param chat   the chat's identifier
	 * @param off    whether the chat is off for them
	 */
	public void setOff(@NotNull UUID player, @NotNull String chat, boolean off) {
		if (off) this.off.put(key(player, chat), true);
		else this.off.remove(key(player, chat));
	}

	private static String key(UUID player, String chat) {
		return player + "/" + chat;
	}
}
