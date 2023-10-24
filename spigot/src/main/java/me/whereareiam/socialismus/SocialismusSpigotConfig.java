package me.whereareiam.socialismus;

import me.whereareiam.socialismus.platform.PlatformMessageSender;
import org.bukkit.plugin.Plugin;

public class SocialismusSpigotConfig extends SocialismusConfig {
    public SocialismusSpigotConfig(Plugin plugin) {
        super(plugin);
    }

    @Override
    protected void configurePlatformSpecifics() {
        bind(PlatformMessageSender.class).to(SpigotMessageSender.class);
    }
}

