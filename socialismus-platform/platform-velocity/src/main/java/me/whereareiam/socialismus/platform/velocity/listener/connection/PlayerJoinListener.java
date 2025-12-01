package me.whereareiam.socialismus.platform.velocity.listener.connection;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.common.SynchronizationService;
import me.whereareiam.socialismus.output.listener.DynamicListener;
import me.whereareiam.socialismus.registry.PlayerRegistry;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PlayerJoinListener implements DynamicListener<PlayerChooseInitialServerEvent> {
	private final PlayerRegistry playerRegistry;
	private final SynchronizationService syncService;

	public void onEvent(PlayerChooseInitialServerEvent event) {
		String serverName = event.getInitialServer()
				.map(serverConnection -> serverConnection.getServerInfo().getName())
				.orElse(null);

		playerRegistry.getPlayerData(event.getPlayer().getUniqueId()).ifPresent(
				player -> {
					// Update server name
					player.setServer(serverName);
					// Apply synchronization settings
					syncService.applyTo(player, serverName);
				}
		);
	}
}
