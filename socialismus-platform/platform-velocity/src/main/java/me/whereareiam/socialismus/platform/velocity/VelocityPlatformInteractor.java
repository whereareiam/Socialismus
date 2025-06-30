package me.whereareiam.socialismus.platform.velocity;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.position.Position;
import me.whereareiam.socialismus.api.output.PlatformInteractor;
import me.whereareiam.socialismus.api.type.Version;
import net.kyori.adventure.text.Component;

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
	public void broadcast(Component component) {
		proxyServer.sendMessage(component);
	}

	@Override
	public boolean areWithinRange(UUID player1, UUID player2, double range) {
		// TODO: Sync

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
	public int getOnlinePlayersCount() {
		return proxyServer.getPlayerCount();
	}

	@Override
	public Version getServerVersion() {
		return Version.getLatest();
	}

	@Override
	public String getServerIp() {
		return proxyServer.getBoundAddress().getHostString();
	}

	@Override
	public int getServerPort() {
		return proxyServer.getBoundAddress().getPort();
	}
}