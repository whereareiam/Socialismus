package me.whereareiam.socialismus.command.suggestion;

import com.google.inject.Inject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Actor;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.service.PlatformInteractor;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class CrossPlayerProvider {
	private final PlatformInteractor interactor;
	private final PlayerRegistry playerRegistry;

	/**
	 * Suggests the players of this server and those a module adds, such as the players on the other servers of a
	 * network, leaving out the players the player who completes may not see.
	 */
	@Suggestions("crossPlayers")
	public Collection<String> suggestCrossPlayers(@NotNull CommandContext<Actor> context) {
		Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		names.addAll(interactor.getOnlinePlayers());
		playerRegistry.getKnownPlayers().forEach(player -> names.add(player.getUsername()));

		return names.stream()
				.filter(name -> visibleTo(context.sender(), name))
				.toList();
	}

	private boolean visibleTo(Actor viewer, String name) {
		if (!(viewer instanceof SocialismusPlayer player)) return true;

		return playerRegistry.findPlayer(name).map(player::canSee).orElse(true);
	}
}
