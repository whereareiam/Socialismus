package me.whereareiam.socialismus.command.suggestion;

import com.google.inject.Inject;
import com.google.inject.Provider;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.service.PlatformInteractor;
import me.whereareiam.socialismus.service.resource.CacheService;
import org.incendo.cloud.annotations.suggestion.Suggestions;

import java.util.Collection;
import java.util.Set;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class CrossPlayerProvider {
	private static final String SET_KEY = Constants.Channels.PLAYERS;

	private final Provider<CacheService> cache;
	private final PlatformInteractor interactor;

	@Suggestions("crossPlayers")
	public Collection<String> suggestCrossPlayers() {
		if (!Constants.Synchronization.SYNCHRONIZATION || !Constants.Synchronization.CROSS_PLAYER_SYNC)
			return interactor.getOnlinePlayers();

		final CacheService cache = this.cache.get();

		Set<String> players = cache.get(SET_KEY);
		return players.stream()
				.sorted(String.CASE_INSENSITIVE_ORDER)
				.toList();
	}
}