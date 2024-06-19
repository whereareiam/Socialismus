package me.whereareiam.socialismus.core.platform;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

public abstract class PlatformCommunicator {
	public abstract void sendMessage(Player player, Component message);

	public abstract void sendActionBar(Player player, Component message);

	public abstract void showTitle(Player player, Title title);

	public abstract void showBossBar(Player player, BossBar bossBar);

	public abstract void hideBossBar(Player player, BossBar bossBar);

	public interface MessageUtil {
		void sendMessage(Player player, Component message);

		void sendActionBar(Player player, Component message);

		void showTitle(Player player, Title title);

		void showBossBar(Player player, BossBar bossBar);

		void hideBossBar(Player player, BossBar bossBar);
	}
}
