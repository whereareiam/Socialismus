package me.whereareiam.socialismus.platform.velocity;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.velocitypowered.api.proxy.ProxyServer;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.position.Position;
import me.whereareiam.socialismus.api.output.PlatformInteractor;
import me.whereareiam.socialismus.api.output.integration.Integration;
import me.whereareiam.socialismus.api.output.integration.SynchronizationIntegration;
import me.whereareiam.socialismus.api.type.Version;
import net.kyori.adventure.text.Component;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Singleton
public class VelocityPlatformInteractor implements PlatformInteractor {
	private final ProxyServer proxyServer;
	private final Provider<Set<Integration>> integrations;

	@Inject
	public VelocityPlatformInteractor(ProxyServer proxyServer, Provider<Set<Integration>> integrations) {
		this.proxyServer = proxyServer;
		this.integrations = integrations;
	}

	@Override
	public void broadcast(Component component) {
		proxyServer.sendMessage(component);
	}

	@Override
	public boolean areWithinRange(UUID player1, UUID player2, double range) {
		if (integrations.get().stream().anyMatch(i -> i instanceof SynchronizationIntegration)) {
			Optional<SynchronizationIntegration> syncIntegration = integrations.get().stream()
					.filter(i -> i instanceof SynchronizationIntegration)
					.map(i -> (SynchronizationIntegration) i)
					.findFirst();

			if (syncIntegration.isPresent()) {
				Optional<String> location1 = syncIntegration.get().getLocation(player1);
				Optional<String> location2 = syncIntegration.get().getLocation(player2);

				if (location1.isPresent() && location2.isPresent())
					return location1.get().equals(location2.get());
			}
		}

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
	public Position getPosition(DummyPlayer dummyPlayer) {
		throw new UnsupportedOperationException("Velocity does not support getting player position directly.");
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
}