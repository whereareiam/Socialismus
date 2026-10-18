package me.whereareiam.socialismus.module.dialogue.common.conversation;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.player.SocialismusPlayerRemovedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The partners of the players in conversation mode on this server, by the players' unique ids. A player leaves
 * conversation mode when they leave the server, once Dialogue registers this listener.
 */
@Singleton
public class ConversationModes implements EventListener {
	private final Map<UUID, String> partners = new ConcurrentHashMap<>();

	/**
	 * Returns the partner of a player in conversation mode.
	 */
	@NotNull
	public Optional<String> partnerOf(@NotNull UUID player) {
		return Optional.ofNullable(partners.get(player));
	}

	/**
	 * Puts a player in conversation mode with a partner, replacing any partner they had.
	 */
	public void enter(@NotNull UUID player, @NotNull String partner) {
		partners.put(player, partner);
	}

	/**
	 * Takes a player out of conversation mode.
	 */
	public void leave(@NotNull UUID player) {
		partners.remove(player);
	}

	@SocialisticEvent
	public void onPlayerRemoved(@NotNull SocialismusPlayerRemovedEvent event) {
		leave(event.getPlayer().getUniqueId());
	}
}
