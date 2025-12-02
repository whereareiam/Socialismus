package me.whereareiam.socialismus.platform.velocity;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.velocitypowered.api.plugin.PluginContainer;
import me.whereareiam.socialismus.module.PlatformClassLoader;

@Singleton
public class VelocityClassLoader implements PlatformClassLoader {
    private final PluginContainer plugin;

    @Inject
    public VelocityClassLoader(PluginContainer plugin) {
        this.plugin = plugin;
    }

    @Override
    public ClassLoader getClassLoader() {
        return plugin.getInstance().map(object -> object.getClass().getClassLoader()).orElse(null);
    }
}
