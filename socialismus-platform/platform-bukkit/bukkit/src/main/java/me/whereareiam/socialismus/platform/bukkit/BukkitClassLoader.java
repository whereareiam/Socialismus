package me.whereareiam.socialismus.platform.bukkit;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.output.PlatformClassLoader;
import org.bukkit.plugin.Plugin;

@Singleton
public class BukkitClassLoader implements PlatformClassLoader {
    private final Plugin plugin;

    @Inject
    public BukkitClassLoader(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ClassLoader getClassLoader() {
        return plugin.getClass().getClassLoader();
    }
}
