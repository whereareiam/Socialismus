package me.whereareiam.socialismus.platform.bukkit.inject;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import me.whereareiam.socialismus.api.input.DependencyResolver;
import me.whereareiam.socialismus.api.model.player.DummyPlayer;
import me.whereareiam.socialismus.api.output.LoggingHelper;
import me.whereareiam.socialismus.api.output.PlatformClassLoader;
import me.whereareiam.socialismus.api.output.PlatformInteractor;
import me.whereareiam.socialismus.api.output.Scheduler;
import me.whereareiam.socialismus.api.output.listener.ListenerRegistrar;
import me.whereareiam.socialismus.integration.bstats.Metrics;
import me.whereareiam.socialismus.platform.bukkit.*;
import me.whereareiam.socialismus.platform.bukkit.listener.BukkitListenerRegistrar;
import me.whereareiam.socialismus.shared.Constants;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.incendo.cloud.CommandManager;

public class BukkitInjectorConfiguration extends AbstractModule {
    private final Plugin plugin;
    private final BukkitDependencyResolver dependencyResolver;

    public BukkitInjectorConfiguration(Plugin plugin, BukkitDependencyResolver dependencyResolver) {
        this.plugin = plugin;
        this.dependencyResolver = dependencyResolver;
    }

    @Override
    protected void configure() {
        bind(Plugin.class).toInstance(plugin);
        bind(PluginManager.class).toInstance(plugin.getServer().getPluginManager());
        bind(BukkitAudiences.class).toProvider(BukkitAudiencesProvider.class);
        bind(DependencyResolver.class).toInstance(dependencyResolver);

        bind(LoggingHelper.class).to(BukkitLoggingHelper.class);
        bind(Scheduler.class).to(BukkitScheduler.class);
        bind(ListenerRegistrar.class).to(BukkitListenerRegistrar.class);
        bind(PlatformInteractor.class).to(BukkitPlatformInteractor.class);
        bind(PlatformClassLoader.class).to(BukkitClassLoader.class);
        bind(new TypeLiteral<CommandManager<DummyPlayer>>() {}).toProvider(BukkitCommandManagerProvider.class);

        bind(org.bstats.bukkit.Metrics.class).toInstance(new org.bstats.bukkit.Metrics(plugin, Constants.BStats.BUKKIT_ID));
        bind(Metrics.class).to(BukkitMetrics.class);
    }
}
