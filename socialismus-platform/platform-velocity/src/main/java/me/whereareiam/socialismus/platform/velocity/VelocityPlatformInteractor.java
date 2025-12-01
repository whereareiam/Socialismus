package me.whereareiam.socialismus.platform.velocity;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.output.PlatformInteractor;
import me.whereareiam.socialismus.type.BroadcastTarget;
import me.whereareiam.socialismus.type.Version;
import net.kyori.adventure.text.Component;

import java.util.List;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class VelocityPlatformInteractor implements PlatformInteractor {
	private final ProxyServer proxyServer;

	@Override
	public void broadcast(Component component, BroadcastTarget target) {
		switch (target) {
			case ALL -> proxyServer.sendMessage(component);
			case PLAYERS -> proxyServer.getAllPlayers().forEach(player -> player.sendMessage(component));
			case CONSOLE -> proxyServer.getConsoleCommandSource().sendMessage(component);
		}
	}

	@Override
	public boolean hasPermission(String username, String permission) {
		return proxyServer
				.getPlayer(username)
				.map(value -> value.hasPermission(permission))
				.orElse(false);
	}

	@Override
	public List<String> getOnlinePlayers() {
		return proxyServer.getAllPlayers().stream()
				.map(Player::getUsername)
				.toList();
	}

	@Override
	public int getOnlinePlayersCount() {
		return proxyServer.getPlayerCount();
	}

	@Override
	public Version getServerVersion() {
		return Version.getLatest();
	}
}