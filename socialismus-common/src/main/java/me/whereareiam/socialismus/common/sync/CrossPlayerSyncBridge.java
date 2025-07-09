package me.whereareiam.socialismus.common.sync;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.input.container.PlayerContainerService;
import me.whereareiam.socialismus.api.input.event.EventListener;
import me.whereareiam.socialismus.api.input.event.EventManager;
import me.whereareiam.socialismus.api.input.event.base.SocialisticEvent;
import me.whereareiam.socialismus.api.input.event.player.DummyPlayerAddedEvent;
import me.whereareiam.socialismus.api.input.event.player.DummyPlayerRemovedEvent;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.output.resource.CacheService;
import me.whereareiam.socialismus.shared.Constants;

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
		System.out.println("Add: " + SET_KEY);
		System.out.println("Add: " + event.getDummyPlayer().getUsername());
		System.out.println(">>> CacheService impl = "
				+ cache.getClass().getName()
				+ "@" + System.identityHashCode(cache));

		cache.add(SET_KEY, event.getDummyPlayer().getUsername());
		System.out.println(cache.get(SET_KEY));
	}

	@SocialisticEvent
	public void onDummyPlayerRemove(DummyPlayerRemovedEvent event) {
		System.out.println("Remove: " + event.getDummyPlayer().getUsername());
		cache.remove(SET_KEY, event.getDummyPlayer().getUsername());
	}
}
