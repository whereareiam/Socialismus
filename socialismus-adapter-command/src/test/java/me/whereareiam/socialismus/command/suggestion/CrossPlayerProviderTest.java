package me.whereareiam.socialismus.command.suggestion;

import me.whereareiam.keystone.Actor;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.service.PlatformInteractor;
import org.incendo.cloud.context.CommandContext;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CrossPlayerProviderTest {
	private final PlatformInteractor interactor = mock(PlatformInteractor.class);
	private final PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
	private final CrossPlayerProvider provider = new CrossPlayerProvider(interactor, playerRegistry);

	@Test
	@SuppressWarnings("unchecked")
	void leavesOutThePlayersTheCompletingPlayerMayNotSee() {
		SocialismusPlayer alice = player("Alice");
		SocialismusPlayer bob = player("Bob");
		SocialismusPlayer carol = player("Carol");
		when(interactor.getOnlinePlayers()).thenReturn(List.of("Carol", "Bob", "Alice"));
		when(alice.canSee(any())).thenReturn(true);
		when(alice.canSee(bob)).thenReturn(false);
		CommandContext<Actor> context = mock(CommandContext.class);
		when(context.sender()).thenReturn(alice);

		assertEquals(List.of("Alice", "Carol"), List.copyOf(provider.suggestCrossPlayers(context)));
	}

	@Test
	@SuppressWarnings("unchecked")
	void suggestsEveryoneToTheConsole() {
		when(interactor.getOnlinePlayers()).thenReturn(List.of("Bob", "Alice"));
		when(playerRegistry.findPlayer(anyString())).thenReturn(Optional.empty());
		CommandContext<Actor> context = mock(CommandContext.class);
		when(context.sender()).thenReturn(mock(Actor.class));

		assertEquals(List.of("Alice", "Bob"), List.copyOf(provider.suggestCrossPlayers(context)));
	}

	@Test
	@SuppressWarnings("unchecked")
	void suggestsThePlayersAModuleAddsAsWell() {
		SocialismusPlayer remote = player("Dave");
		when(interactor.getOnlinePlayers()).thenReturn(List.of("Alice"));
		when(playerRegistry.getKnownPlayers()).thenReturn(List.of(remote));
		when(playerRegistry.findPlayer("Alice")).thenReturn(Optional.empty());
		CommandContext<Actor> context = mock(CommandContext.class);
		when(context.sender()).thenReturn(mock(Actor.class));

		assertEquals(List.of("Alice", "Dave"), List.copyOf(provider.suggestCrossPlayers(context)));
	}

	private SocialismusPlayer player(String name) {
		SocialismusPlayer player = mock(SocialismusPlayer.class);
		when(player.getUsername()).thenReturn(name);
		when(playerRegistry.findPlayer(name)).thenReturn(Optional.of(player));

		return player;
	}
}
