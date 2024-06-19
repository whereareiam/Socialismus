package me.whereareiam.socialismus.spigot;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.core.platform.PlatformCommunicator;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Singleton
public class SpigotCommunicator extends PlatformCommunicator {
	private final BukkitAudiences audiences;

	@Inject
	public SpigotCommunicator(Plugin plugin) {
		this.audiences = BukkitAudiences.create(plugin);
	}

	@Override
	public void sendMessage(Player player, Component message) {
		audiences.sender(player).sendMessage(message);
	}

	@Override
	public void sendActionBar(Player player, Component message) {
		audiences.sender(player).sendActionBar(message);
	}

	@Override
	public void showTitle(Player player, Title title) {
		audiences.sender(player).showTitle(title);
	}

	@Override
	public void showBossBar(Player player, BossBar bossBar) {
		audiences.sender(player).showBossBar(bossBar);
	}

	@Override
	public void hideBossBar(Player player, BossBar bossBar) {
		audiences.sender(player).hideBossBar(bossBar);
	}
}

