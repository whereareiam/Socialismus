package me.whereareiam.socialismus.command.suggestion;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.keystone.Actor;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global suggestion provider for player names.
 * Can be used by any command that needs to suggest player names.
 */
@Singleton
public class PlayerSuggestionProvider {
	private final PlayerRegistry playerRegistry;

	@Inject
	public PlayerSuggestionProvider(@NotNull PlayerRegistry playerRegistry) {
		this.playerRegistry = playerRegistry;
	}

	/**
	 * Provides player name suggestions filtered by input.
	 * Returns all usernames from players currently tracked in the registry.
	 *
	 * @param context the command context
	 * @param input the current input string
	 * @return list of player name suggestions
	 */
	@Suggestions("players")
	public @NotNull List<@NotNull Suggestion> suggestPlayers(
			@NotNull CommandContext<Actor> context,
			@NotNull String input
	) {
		String lowerInput = input.toLowerCase();
		return playerRegistry.getPlayers().stream()
				.map(SocialismusPlayer::getUsername)
				.filter(username -> username.toLowerCase().startsWith(lowerInput))
				.map(Suggestion::suggestion)
				.collect(Collectors.toList());
	}
}

