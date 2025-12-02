package me.whereareiam.socialismus.common.sync;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.player.SocialismusPlayerAddedEvent;
import me.whereareiam.socialismus.event.player.SocialismusPlayerRemovedEvent;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.service.resource.CacheService;
import me.whereareiam.socialismus.registry.PlayerRegistry;

import java.util.Collection;

/**
 * Keeps a distributed Redis Set of all online player names.
 * Key format: <plugin-identifier>:players (e.g. "socialismus:players")
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class CrossPlayerSyncBridge implements EventListener {
	private static final String SET_KEY = Constants.Channels.PLAYERS;

	private final CacheService cache;
	private final EventManager eventManager;
	private final PlayerRegistry localPlayersRegistry;

	public void initialize() {
		eventManager.register(this);

		Collection<SocialismusPlayer> current = localPlayersRegistry.getPlayers();
		current.forEach(p -> cache.add(SET_KEY, p.getUsername()));
	}

	@SocialisticEvent
	public void onSocialismusPlayerAdd(SocialismusPlayerAddedEvent event) {
		cache.add(SET_KEY, event.getPlayer().getUsername());
	}

	@SocialisticEvent
	public void onSocialismusPlayerRemove(SocialismusPlayerRemovedEvent event) {
		cache.remove(SET_KEY, event.getPlayer().getUsername());
	}
}
