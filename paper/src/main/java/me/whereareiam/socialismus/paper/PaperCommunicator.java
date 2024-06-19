package me.whereareiam.socialismus.paper;

import me.whereareiam.socialismus.core.platform.PlatformCommunicator;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

public class PaperCommunicator extends PlatformCommunicator {
	@Override
	public void sendMessage(Player player, Component message) {
		player.sendMessage(message);
	}

	@Override
	public void sendActionBar(Player player, Component message) {
		player.sendActionBar(message);
	}

	@Override
	public void showTitle(Player player, Title title) {
		player.showTitle(title);
	}

	@Override
	public void showBossBar(Player player, BossBar bossBar) {
		player.showBossBar(bossBar);
	}

	@Override
	public void hideBossBar(Player player, BossBar bossBar) {
		player.hideBossBar(bossBar);
	}
}


