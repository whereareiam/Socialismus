package me.whereareiam.socialismus.platform;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.service.PlatformInteractor;
import me.whereareiam.socialismus.type.Version;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public abstract class AbstractPlatformInteractor implements PlatformInteractor {
	@Override
	public boolean hasPermission(String username, String permission) {
		Player player = Bukkit.getPlayer(username);
		if (player == null) return false;

		return player.hasPermission(permission);
	}

	@Override
	public List<String> getOnlinePlayers() {
		return Bukkit.getOnlinePlayers().stream()
				.map(Player::getName)
				.toList();
	}

	@Override
	public int getOnlinePlayersCount() {
		return Bukkit.getOnlinePlayers().size();
	}

	@Override
	public Version getServerVersion() {
		return Version.of(Bukkit.getBukkitVersion());
	}
}
