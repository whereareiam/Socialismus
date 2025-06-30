package me.whereareiam.socialismus.platform.paper;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.platform.AbstractPlatformInteractor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

@Singleton
public class PaperPlatformInteractor extends AbstractPlatformInteractor {
	@Override
	public void broadcast(Component component) {
		Bukkit.broadcast(component);
	}
}
