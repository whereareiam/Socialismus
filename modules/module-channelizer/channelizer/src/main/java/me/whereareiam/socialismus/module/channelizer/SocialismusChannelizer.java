package me.whereareiam.socialismus.module.channelizer;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.resource.ProvidedResource;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.module.channelizer.api.PlatformMessageBus;
import me.whereareiam.socialismus.module.channelizer.network.ChannelizerNetworkResources;
import me.whereareiam.socialismus.module.channelizer.platform.bukkit.BukkitInjectorConfiguration;
import me.whereareiam.socialismus.service.resource.ResourceProvider;
import me.whereareiam.socialismus.type.PlatformType;

import java.util.List;

/**
 * Carries SocialismusNetwork's messages between the servers of a network as plugin messages, which the proxy forwards.
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SocialismusChannelizer extends SocialisticModule implements ResourceProvider {
	private final Injector parentInjector;
	private Injector injector;

	@Override
	public void onLoad() {
		injector = Guice.createInjector(platformModule());
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onUnload() {
	}

	@Override
	public List<ProvidedResource<?>> provideResources() {
		return ChannelizerNetworkResources.provide(injector.getInstance(PlatformMessageBus.class));
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
