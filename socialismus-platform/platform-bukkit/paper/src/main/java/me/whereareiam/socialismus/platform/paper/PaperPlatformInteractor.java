package me.whereareiam.socialismus.platform.paper;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.platform.AbstractPlatformInteractor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Singleton
public class PaperPlatformInteractor extends AbstractPlatformInteractor {
	@Override
	public void broadcast(Component component) {
		Bukkit.broadcast(component);
	}

	@Override
	public void broadcast(Component component, boolean silent) {
		if (silent) {
			broadcast(component);
			return;
		}

		for (Player player : Bukkit.getOnlinePlayers())
			player.sendMessage(component);
	}
}
