package me.whereareiam.socialismus.platform.paper.inject;

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
import me.whereareiam.socialismus.platform.paper.*;
import me.whereareiam.socialismus.platform.paper.listener.PaperListenerRegistrar;
import me.whereareiam.socialismus.shared.Constants;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.CommandManager;

public class PaperInjectorConfiguration extends AbstractModule {
    private final Plugin plugin;
    private final PaperDependencyResolver dependencyResolver;

    public PaperInjectorConfiguration(Plugin plugin, PaperDependencyResolver dependencyResolver) {
        this.plugin = plugin;
        this.dependencyResolver = dependencyResolver;
    }

    @Override
    protected void configure() {
        bind(Plugin.class).toInstance(plugin);
        bind(PluginManager.class).toInstance(plugin.getServer().getPluginManager());
        bind(DependencyResolver.class).toInstance(dependencyResolver);

        bind(LoggingHelper.class).to(PaperLoggingHelper.class);
        bind(Scheduler.class).to(PaperScheduler.class);
        bind(ListenerRegistrar.class).to(PaperListenerRegistrar.class);
        bind(PlatformInteractor.class).to(PaperPlatformInteractor.class);
        bind(PlatformClassLoader.class).to(PaperClassLoader.class);
        bind(new TypeLiteral<CommandManager<DummyPlayer>>() {}).toProvider(PaperCommandManagerProvider.class);

        bind(org.bstats.bukkit.Metrics.class).toInstance(new org.bstats.bukkit.Metrics((JavaPlugin) plugin, Constants.BStats.BUKKIT_ID));
        bind(Metrics.class).to(PaperMetrics.class);
    }
}
