package me.whereareiam.socialismus.platform.paper.inject;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Actor;
import me.whereareiam.socialismus.listener.ListenerRegistrar;
import me.whereareiam.socialismus.platform.paper.PaperCommandManagerProvider;
import me.whereareiam.socialismus.platform.paper.PaperDependencyResolver;
import me.whereareiam.socialismus.platform.paper.PaperPlatformInteractor;
import me.whereareiam.socialismus.platform.paper.PaperScheduler;
import me.whereareiam.socialismus.platform.paper.listener.PaperListenerRegistrar;
import me.whereareiam.socialismus.service.DependencyResolver;
import me.whereareiam.socialismus.service.PlatformInteractor;
import me.whereareiam.socialismus.service.Scheduler;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.incendo.cloud.CommandManager;

@RequiredArgsConstructor
public class PaperInjectorConfiguration extends AbstractModule {
	private final Plugin plugin;
	private final PaperDependencyResolver dependencyResolver;

	@Override
	protected void configure() {
		bind(Plugin.class).toInstance(plugin);
		bind(PluginManager.class).toInstance(plugin.getServer().getPluginManager());
		bind(DependencyResolver.class).toInstance(dependencyResolver);

		bind(Scheduler.class).to(PaperScheduler.class);
		bind(ListenerRegistrar.class).to(PaperListenerRegistrar.class);
		bind(PlatformInteractor.class).to(PaperPlatformInteractor.class);
		bind(new TypeLiteral<CommandManager<Actor>>() {}).toProvider(PaperCommandManagerProvider.class);
	}
}
