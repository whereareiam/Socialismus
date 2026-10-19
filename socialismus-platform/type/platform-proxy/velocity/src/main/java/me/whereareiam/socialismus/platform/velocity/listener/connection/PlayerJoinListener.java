package me.whereareiam.socialismus.platform.velocity.listener.connection;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.listener.DynamicListener;
import me.whereareiam.socialismus.registry.PlayerRegistry;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PlayerJoinListener implements DynamicListener<PlayerChooseInitialServerEvent> {
	private final PlayerRegistry playerRegistry;

	public void onEvent(PlayerChooseInitialServerEvent event) {
		String serverName = event.getInitialServer()
				.map(serverConnection -> serverConnection.getServerInfo().getName())
				.orElse(null);

		playerRegistry.getPlayerData(event.getPlayer().getUniqueId()).ifPresent(player -> player.setServer(serverName));
	}
}
