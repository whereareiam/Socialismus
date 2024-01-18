package me.whereareiam.socialismus.spigot;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.core.platform.PlatformMessageSender;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@Singleton
public class SpigotMessageSender extends PlatformMessageSender {
	private final BukkitAudiences audiences;

	@Inject
	public SpigotMessageSender(Plugin plugin) {
		this.audiences = BukkitAudiences.create(plugin);
	}

	@Override
	public void sendMessage(Player player, Component message) {
		audiences.sender(player).sendMessage(message);
	}
}

