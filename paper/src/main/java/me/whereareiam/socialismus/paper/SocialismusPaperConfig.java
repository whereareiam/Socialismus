package me.whereareiam.socialismus.paper;

import me.whereareiam.socialismus.core.SocialismusConfig;
import me.whereareiam.socialismus.core.platform.PlatformMessageSender;
import org.bukkit.plugin.Plugin;

public class SocialismusPaperConfig extends SocialismusConfig {
	public SocialismusPaperConfig(Plugin plugin) {
		super(plugin);
	}

	@Override
	protected void configurePlatformSpecifics() {
		bind(PlatformMessageSender.class).to(PaperMessageSender.class);
	}
}
