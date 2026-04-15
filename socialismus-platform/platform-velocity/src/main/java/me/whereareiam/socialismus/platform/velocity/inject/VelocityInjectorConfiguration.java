package me.whereareiam.socialismus.platform.velocity.inject;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import me.whereareiam.keystone.Actor;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.integration.bstats.Metrics;
import me.whereareiam.socialismus.integration.bstats.bStatsIntegration;
import me.whereareiam.socialismus.integration.packetevents.PacketEventsIntegration;
import me.whereareiam.socialismus.integration.papiproxybridge.PAPIProxyBridgeIntegration;
import me.whereareiam.socialismus.listener.ListenerRegistrar;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.module.PlatformClassLoader;
import me.whereareiam.socialismus.platform.velocity.*;
import me.whereareiam.socialismus.platform.velocity.listener.VelocityListenerRegistrar;
import me.whereareiam.socialismus.service.DependencyResolver;
import me.whereareiam.socialismus.service.PlatformInteractor;
import me.whereareiam.socialismus.service.Scheduler;
import org.incendo.cloud.CommandManager;
import org.slf4j.Logger;

public class VelocityInjectorConfiguration extends AbstractModule {
    private final VelocitySocialismus socialismus;
    private final PluginContainer plugin;
    private final ProxyServer proxyServer;
    private final VelocityDependencyResolver dependencyResolver;

    public VelocityInjectorConfiguration(VelocitySocialismus socialismus, PluginContainer plugin, ProxyServer proxyServer, VelocityDependencyResolver dependencyResolver) {
        this.socialismus = socialismus;
        this.plugin = plugin;
        this.proxyServer = proxyServer;
        this.dependencyResolver = dependencyResolver;
    }

    @Override
    protected void configure() {
        bind(VelocitySocialismus.class).toInstance(socialismus);
        bind(PluginContainer.class).toInstance(plugin);
        bind(ProxyServer.class).toInstance(proxyServer);
        bind(EventManager.class).toInstance(proxyServer.getEventManager());
        bind(Logger.class).toInstance(socialismus.getLogger());
        bind(DependencyResolver.class).toInstance(dependencyResolver);

        bind(LoggingHelper.class).to(VelocityLoggingHelper.class);
        bind(Scheduler.class).to(VelocityScheduler.class);
        bind(ListenerRegistrar.class).to(VelocityListenerRegistrar.class);
        bind(PlatformInteractor.class).to(VelocityPlatformInteractor.class);
        bind(PlatformClassLoader.class).to(VelocityClassLoader.class);
        bind(new TypeLiteral<CommandManager<Actor>>() {}).toProvider(VelocityCommandManagerProvider.class);

        bind(Metrics.class).to(VelocityMetrics.class);

        Multibinder<Integration> integrations = Multibinder.newSetBinder(binder(), Integration.class, Names.named("integrationCandidates"));
        integrations.addBinding().to(PAPIProxyBridgeIntegration.class);
        integrations.addBinding().to(PacketEventsIntegration.class);
        integrations.addBinding().to(bStatsIntegration.class);
    }
}
