package me.whereareiam.socialismus;

import co.aikar.commands.BukkitCommandManager;
import com.google.inject.AbstractModule;
import org.bukkit.plugin.Plugin;

public class SocialismusConfig extends AbstractModule {
    private final Plugin plugin;

    public SocialismusConfig(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(Plugin.class).toInstance(plugin);
        bind(BukkitCommandManager.class).toInstance(new BukkitCommandManager(plugin));
    }
}