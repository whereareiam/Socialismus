package me.whereareiam.socialismus.command.suggestion;

import com.google.inject.Inject;
import com.google.inject.Provider;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Actor;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.service.PlatformInteractor;
import me.whereareiam.socialismus.service.resource.CacheService;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class CrossPlayerProvider {
	private static final String SET_KEY = Constants.Channels.PLAYERS;

	private final Provider<CacheService> cache;
	private final PlatformInteractor interactor;
	private final PlayerRegistry playerRegistry;

	/**
	 * Suggests the players of this server, or of every server when players are synced, leaving out the players of
	 * this server the player who completes may not see.
	 */
	@Suggestions("crossPlayers")
	public Collection<String> suggestCrossPlayers(@NotNull CommandContext<Actor> context) {
		Collection<String> players = !Constants.Synchronization.SYNCHRONIZATION || !Constants.Synchronization.CROSS_PLAYER_SYNC
				? interactor.getOnlinePlayers()
				: this.cache.get().get(SET_KEY);

		return players.stream()
				.filter(name -> visibleTo(context.sender(), name))
				.sorted(String.CASE_INSENSITIVE_ORDER)
				.toList();
	}

	private boolean visibleTo(Actor viewer, String name) {
		if (!(viewer instanceof SocialismusPlayer player)) return true;

		return playerRegistry.getPlayerData(name).map(player::canSee).orElse(true);
	}
}