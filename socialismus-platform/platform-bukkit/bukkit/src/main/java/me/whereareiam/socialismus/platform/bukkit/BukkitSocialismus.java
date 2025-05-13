package me.whereareiam.socialismus.platform.bukkit;

import me.whereareiam.socialismus.api.PluginType;
import me.whereareiam.socialismus.common.CommonInjector;
import me.whereareiam.socialismus.common.CommonSocialismus;
import me.whereareiam.socialismus.common.IntegrityChecker;
import me.whereareiam.socialismus.integration.bstats.bStatsIntegration;
import me.whereareiam.socialismus.integration.packetevents.PacketEventsIntegration;
import me.whereareiam.socialismus.integration.placeholderapi.PlaceholderAPIIntegration;
import me.whereareiam.socialismus.platform.bukkit.inject.BukkitInjector;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class BukkitSocialismus extends JavaPlugin {
    private final CommonSocialismus commonSocialismus = new CommonSocialismus();
    private final Path dataPath = getDataFolder().toPath();
    private final Logger logger = getLogger();

    @Override
    public void onLoad() {
        PluginType.setPluginType(PluginType.BUKKIT);

        BukkitDependencyResolver dependencyResolver = new BukkitDependencyResolver(this);
        dependencyResolver.loadLibraries();
        dependencyResolver.resolveDependencies();

        new BukkitInjector(this, dependencyResolver, dataPath);
        BukkitLoggingHelper.setLogger(logger);

        if (CommonInjector.getInjector().getInstance(IntegrityChecker.class).checkIntegrity())
            getPluginLoader().disablePlugin(this);
    }

    @Override
    public void onEnable() {
        CommonInjector.getInjector().getInstance(PlaceholderAPIIntegration.class);
        CommonInjector.getInjector().getInstance(PacketEventsIntegration.class);
        CommonInjector.getInjector().getInstance(bStatsIntegration.class);

        commonSocialismus.onEnable();
    }

    @Override
    public void onDisable() {
        commonSocialismus.onDisable();
    }
}
