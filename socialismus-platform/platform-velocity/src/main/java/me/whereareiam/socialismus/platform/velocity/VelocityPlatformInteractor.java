package me.whereareiam.socialismus.platform.velocity;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.position.Position;
import me.whereareiam.socialismus.api.output.PlatformInteractor;
import me.whereareiam.socialismus.api.type.BroadcastTarget;
import me.whereareiam.socialismus.api.type.Version;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class VelocityPlatformInteractor implements PlatformInteractor {
	private final ProxyServer proxyServer;

	@Override
	public Position getPosition(DummyPlayer dummyPlayer) {
		throw new UnsupportedOperationException("Velocity does not support getting player position directly.");
	}

	@Override
	public Position getEyePosition(DummyPlayer dummyPlayer) {
		throw new UnsupportedOperationException("Velocity does not support getting player eye position directly.");
	}

	@Override
	public void broadcast(Component component, BroadcastTarget target) {
		switch (target) {
			case ALL -> proxyServer.sendMessage(component);
			case PLAYERS -> proxyServer.getAllPlayers().forEach(player -> player.sendMessage(component));
			case CONSOLE -> proxyServer.getConsoleCommandSource().sendMessage(component);
		}
	}

	@Override
	public boolean areWithinRange(UUID player1, UUID player2, double range) {
		return proxyServer.getPlayer(player1)
				.flatMap(p1 -> proxyServer.getPlayer(player2)
						.flatMap(p2 -> p1.getCurrentServer()
								.flatMap(s1 -> p2.getCurrentServer()
										.map(s1::equals)
								)
						)
				).orElse(false);
	}

	@Override
	public boolean hasPermission(String username, String permission) {
		return proxyServer
				.getPlayer(username)
				.map(value -> value.hasPermission(permission))
				.orElse(false);
	}

	@Override
	public boolean hasPermission(DummyPlayer dummyPlayer, String permission) {
		return proxyServer
				.getPlayer(dummyPlayer.getUniqueId())
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