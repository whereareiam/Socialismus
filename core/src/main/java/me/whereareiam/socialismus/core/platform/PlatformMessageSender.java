package me.whereareiam.socialismus.core.platform;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public abstract class PlatformMessageSender {
	public abstract void sendMessage(Player player, Component message);

	public interface MessageUtil {
		void sendMessage(Player player, Component message);
	}
}
