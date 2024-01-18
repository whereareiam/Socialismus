package me.whereareiam.socialismus.spigot;

import me.whereareiam.socialismus.core.SocialismusConfig;
import me.whereareiam.socialismus.core.platform.PlatformMessageSender;
import org.bukkit.plugin.Plugin;

public class SocialismusSpigotConfig extends SocialismusConfig {
	public SocialismusSpigotConfig(SocialismusSpigot instance, Plugin plugin) {
		super(instance, plugin);
	}

	@Override
	protected void configurePlatformSpecifics() {
		bind(PlatformMessageSender.class).to(SpigotMessageSender.class);
	}
}

