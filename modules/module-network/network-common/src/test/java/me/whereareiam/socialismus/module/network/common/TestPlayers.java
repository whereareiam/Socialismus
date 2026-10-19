package me.whereareiam.socialismus.module.network.common;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Players a test node hosts.
 */
public final class TestPlayers {
	private TestPlayers() {
	}

	public static SocialismusPlayer player(String name, String server) {
		SocialismusPlayer player = mock(SocialismusPlayer.class);
		UUID id = UUID.nameUUIDFromBytes(name.getBytes());
		when(player.getUniqueId()).thenReturn(id);
		when(player.getUsername()).thenReturn(name);
		when(player.getServer()).thenReturn(server);

		return player;
	}
}
