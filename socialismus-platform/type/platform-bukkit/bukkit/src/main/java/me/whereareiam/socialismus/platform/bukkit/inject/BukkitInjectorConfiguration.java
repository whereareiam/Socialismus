package me.whereareiam.socialismus.platform.bukkit.inject;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.Actor;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.integration.bstats.bStatsIntegration;
import me.whereareiam.socialismus.integration.packetevents.PacketEventsIntegration;
import me.whereareiam.socialismus.integration.placeholderapi.PlaceholderAPIIntegration;
import me.whereareiam.socialismus.listener.ListenerRegistrar;
import me.whereareiam.socialismus.platform.bukkit.BukkitAudiencesProvider;
import me.whereareiam.socialismus.platform.bukkit.BukkitCommandManagerProvider;
import me.whereareiam.socialismus.platform.bukkit.BukkitPlatformInteractor;
import me.whereareiam.socialismus.platform.bukkit.BukkitScheduler;
import me.whereareiam.socialismus.platform.bukkit.listener.BukkitListenerRegistrar;
import me.whereareiam.socialismus.service.PlatformInteractor;
import me.whereareiam.socialismus.service.Scheduler;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.incendo.cloud.CommandManager;

@RequiredArgsConstructor
public class BukkitInjectorConfiguration extends AbstractModule {
	private final Plugin plugin;

	@Override
	protected void configure() {
		bind(Plugin.class).toInstance(plugin);
		bind(PluginManager.class).toInstance(plugin.getServer().getPluginManager());
		bind(BukkitAudiences.class).toProvider(BukkitAudiencesProvider.class);

		bind(Scheduler.class).to(BukkitScheduler.class);
		bind(ListenerRegistrar.class).to(BukkitListenerRegistrar.class);
		bind(PlatformInteractor.class).to(BukkitPlatformInteractor.class);
		bind(new TypeLiteral<CommandManager<Actor>>() {}).toProvider(BukkitCommandManagerProvider.class);

		Multibinder<Integration> integrations = Multibinder.newSetBinder(binder(), Integration.class, Names.named("integrationCandidates"));
		integrations.addBinding().to(PlaceholderAPIIntegration.class);
		integrations.addBinding().to(PacketEventsIntegration.class);
		integrations.addBinding().to(bStatsIntegration.class);
	}
}
