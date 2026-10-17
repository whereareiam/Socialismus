package me.whereareiam.socialismus.platform.paper;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.type.BroadcastTarget;
import me.whereareiam.socialismus.platform.bukkit.common.AbstractBukkitPlatformInteractor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

@Singleton
public class PaperPlatformInteractor extends AbstractBukkitPlatformInteractor {
	@Override
	public void broadcast(Component component, BroadcastTarget target) {
		switch (target) {
			case ALL -> Bukkit.broadcast(component);
			case PLAYERS -> Bukkit.getOnlinePlayers()
					.forEach(player -> player.sendMessage(component));
			case CONSOLE -> Bukkit.getConsoleSender().sendMessage(component);
		}
	}

}
