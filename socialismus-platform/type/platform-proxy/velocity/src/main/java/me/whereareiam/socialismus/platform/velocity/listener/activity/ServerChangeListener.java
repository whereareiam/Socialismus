package me.whereareiam.socialismus.platform.velocity.listener.activity;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.ServerInfo;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.player.SocialismusPlayerServerChangedEvent;
import me.whereareiam.socialismus.listener.DynamicListener;
import me.whereareiam.socialismus.registry.PlayerRegistry;

import java.util.Objects;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ServerChangeListener implements DynamicListener<ServerPostConnectEvent> {
	private final PlayerRegistry playerRegistry;
	private final EventManager eventManager;

	public void onEvent(ServerPostConnectEvent event) {
		String server = event.getPlayer().getCurrentServer()
				.map(ServerConnection::getServerInfo)
				.map(ServerInfo::getName)
				.orElse(null);

		playerRegistry.getPlayerData(event.getPlayer().getUniqueId()).ifPresent(player -> {
			String previous = player.getServer();
			player.setServer(server);
			if (!Objects.equals(previous, server))
				eventManager.call(new SocialismusPlayerServerChangedEvent(player, previous, server));
		});
	}
}