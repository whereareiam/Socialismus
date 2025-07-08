package me.whereareiam.socialismus.command.provider;

import jakarta.inject.Singleton;
import org.incendo.cloud.annotations.suggestion.Suggestions;

import java.util.Collection;
import java.util.List;

@Singleton
public class CrossPlayerProvider {
	@Suggestions("crossPlayers")
	public Collection<String> suggestCrossPlayers() {
		// TODO Suggest players from the cross-server player list
		return List.of("player1");
	}
}