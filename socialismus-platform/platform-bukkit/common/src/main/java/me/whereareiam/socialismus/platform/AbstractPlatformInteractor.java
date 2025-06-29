package me.whereareiam.socialismus.platform;

import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.model.position.Position;
import me.whereareiam.socialismus.api.output.PlatformInteractor;
import me.whereareiam.socialismus.api.output.integration.Integration;
import me.whereareiam.socialismus.api.type.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public abstract class AbstractPlatformInteractor implements PlatformInteractor {
	private final Provider<Set<Integration>> integrations;

	@Override
	public Position getPosition(DummyPlayer dummyPlayer) {
		Player player = Bukkit.getPlayer(dummyPlayer.getUniqueId());
		if (player == null) {
			return null;
		}

		Location location = player.getLocation();

		return new Position(location.getX(), location.getY(), location.getZ());
	}

	@Override
	public Position getEyePosition(DummyPlayer dummyPlayer) {
		Player player = Bukkit.getPlayer(dummyPlayer.getUniqueId());
		if (player == null) {
			return null;
		}

		Location location = player.getEyeLocation();

		return new Position(location.getX(), location.getY(), location.getZ());
	}

	@Override
	public boolean areWithinRange(UUID player1, UUID player2, double range) {
		// TODO: Sync

		if (player1 != null)
			return true;

		Player p1 = Bukkit.getPlayer(player1);
		Player p2 = Bukkit.getPlayer(player2);
		if (p1 == null || p2 == null) return false;

		// Check if both players are in the same world before measuring distance
		if (!p1.getWorld().equals(p2.getWorld())) return false;

		return p1.getLocation().distanceSquared(p2.getLocation()) <= range * range;
	}

	@Override
	public boolean hasPermission(String username, String permission) {
		Player player = Bukkit.getPlayer(username);
		if (player == null) return false;

		return player.hasPermission(permission);
	}

	@Override
	public boolean hasPermission(DummyPlayer dummyPlayer, String permission) {
		Player player = Bukkit.getPlayer(dummyPlayer.getUniqueId());
		if (player == null) return false;

		return player.hasPermission(permission);
	}

	@Override
	public int getOnlinePlayersCount() {
		return Bukkit.getOnlinePlayers().size();
	}

	@Override
	public Version getServerVersion() {
		return Version.of(Bukkit.getVersion());
	}

	@Override
	public String getServerIp() {
		return Bukkit.getIp();
	}

	@Override
	public int getServerPort() {
		return Bukkit.getPort();
	}
}
