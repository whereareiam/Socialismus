package me.whereareiam.socialismus.platform.velocity.listener.activity;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.ServerInfo;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.input.container.PlayerContainerService;
import me.whereareiam.socialismus.output.listener.DynamicListener;
import me.whereareiam.socialismus.common.SynchronizationService;

@Singleton
@SuppressWarnings("UnstableApiUsage")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ServerChangeListener implements DynamicListener<ServerPostConnectEvent> {
	private final PlayerContainerService playerContainer;
	private final SynchronizationService syncService;

	public void onEvent(ServerPostConnectEvent event) {
		playerContainer.getPlayer(event.getPlayer().getUniqueId()).ifPresent(
				player -> syncService.applyTo(
						player,
						event.getPlayer().getCurrentServer()
								.map(ServerConnection::getServerInfo)
								.map(ServerInfo::getName)
								.orElse(null)
				)
		);
	}
}