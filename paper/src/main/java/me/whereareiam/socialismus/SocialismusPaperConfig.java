package me.whereareiam.socialismus;

import me.whereareiam.socialismus.platform.PlatformMessageSender;
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
