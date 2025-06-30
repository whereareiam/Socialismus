package me.whereareiam.socialismus.platform.velocity.listener.activity;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.ServerInfo;
import me.whereareiam.socialismus.api.input.container.PlayerContainerService;
import me.whereareiam.socialismus.api.output.listener.DynamicListener;

@Singleton
@SuppressWarnings("UnstableApiUsage")
public class ServerChangeListener implements DynamicListener<ServerPostConnectEvent> {
	private final PlayerContainerService playerContainer;

	@Inject
	public ServerChangeListener(PlayerContainerService playerContainer) {
		this.playerContainer = playerContainer;
	}

	public void onEvent(ServerPostConnectEvent event) {
		playerContainer.getPlayer(event.getPlayer().getUniqueId()).ifPresent(
				player -> player.setServer(event.getPlayer().getCurrentServer().map(ServerConnection::getServerInfo).map(ServerInfo::getName).orElse(null))
		);
	}
}