package me.whereareiam.socialismus.module.channelizer;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.resource.ProvidedResource;
import me.whereareiam.socialismus.module.ModuleContext;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.module.channelizer.api.PlatformMessageBus;
import me.whereareiam.socialismus.module.channelizer.network.ChannelizerNetworkResources;
import me.whereareiam.socialismus.module.channelizer.platform.bukkit.BukkitInjectorConfiguration;
import me.whereareiam.socialismus.service.resource.ResourceProvider;
import me.whereareiam.socialismus.type.PlatformType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Carries SocialismusNetwork's messages between the servers of a network as plugin messages, which the proxy forwards.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SocialismusChannelizer implements SocialisticModule, ResourceProvider {
	private final Injector parentInjector;
	private List<ProvidedResource<?>> resources;

	@Override
	public void enable(@NotNull ModuleContext context) {
		Injector injector = Guice.createInjector(platformModule());
		resources = ChannelizerNetworkResources.provide(injector.getInstance(PlatformMessageBus.class));
	}

	@Override
	public List<ProvidedResource<?>> provideResources() {
		return resources;
	}

	private Module platformModule() {
		if (PlatformType.isGameServer())
			return new BukkitInjectorConfiguration(parentInjector);

		return new AbstractModule() {
			@Override
			protected void configure() {
				throw new UnsupportedOperationException("Platform not supported: " + PlatformType.getType().name());
			}
		};
	}
}
