package me.whereareiam.socialismus.platform.bukkit;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.Plugin;

@Singleton
public class BukkitAudiencesProvider implements Provider<BukkitAudiences> {
    private final Plugin plugin;
    private BukkitAudiences audiences;

    @Inject
    public BukkitAudiencesProvider(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public BukkitAudiences get() {
        if (audiences == null) audiences = BukkitAudiences.create(plugin);

        return audiences;
    }
}
