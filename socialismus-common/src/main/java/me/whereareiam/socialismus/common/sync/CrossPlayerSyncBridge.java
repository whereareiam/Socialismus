package me.whereareiam.socialismus.common.sync;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.input.container.PlayerContainerService;
import me.whereareiam.socialismus.input.event.EventListener;
import me.whereareiam.socialismus.input.event.EventManager;
import me.whereareiam.socialismus.input.event.base.SocialisticEvent;
import me.whereareiam.socialismus.input.event.player.DummyPlayerAddedEvent;
import me.whereareiam.socialismus.input.event.player.DummyPlayerRemovedEvent;
import me.whereareiam.socialismus.model.player.DummyPlayer;
import me.whereareiam.socialismus.output.resource.CacheService;

import java.util.Set;

/**
 * Keeps a distributed Redis Set of all online player names.
 * Key format: <plugin-identifier>:players  (e.g. "socialismus:players")
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class CrossPlayerSyncBridge implements EventListener {
	private static final String SET_KEY = Constants.Channels.PLAYERS;

	private final CacheService cache;
	private final EventManager eventManager;
	private final PlayerContainerService localPlayers;

	public void initialize() {
		eventManager.register(this);

		Set<DummyPlayer> current = localPlayers.getPlayers();
		current.forEach(p -> cache.add(SET_KEY, p.getUsername()));
	}

	@SocialisticEvent
	public void onDummyPlayerAdd(DummyPlayerAddedEvent event) {
		cache.add(SET_KEY, event.getDummyPlayer().getUsername());
	}

	@SocialisticEvent
	public void onDummyPlayerRemove(DummyPlayerRemovedEvent event) {
		cache.remove(SET_KEY, event.getDummyPlayer().getUsername());
	}
}
