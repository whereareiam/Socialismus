package me.whereareiam.socialismus.module.channelizer;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.model.resource.ProvidedResource;
import me.whereareiam.socialismus.module.ModuleService;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.module.channelizer.api.PlatformMessageBus;
import me.whereareiam.socialismus.module.channelizer.network.ChannelizerNetworkResources;
import me.whereareiam.socialismus.module.channelizer.platform.bukkit.BukkitInjectorConfiguration;
import me.whereareiam.socialismus.module.channelizer.platform.bukkit.CommonConfiguration;
import me.whereareiam.socialismus.service.resource.ResourceProvider;
import me.whereareiam.socialismus.service.resource.sync.SyncService;
import me.whereareiam.socialismus.type.PlatformType;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SocialismusChannelizer extends SocialisticModule implements ResourceProvider {
	/**
	 * The module whose API this module reaches only while it is loaded, as an optional dependency.
	 */
	private static final String NETWORK = "SocialismusNetwork";

	private final Injector parentInjector;
	private final ModuleService modules;
	private Injector injector;

	@Override
	public void onLoad() {
		injector =
				Guice.createInjector(
						new CommonConfiguration(),
						platformModule()
				);
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
		List<ProvidedResource<?>> resources = new ArrayList<>(List.of(
				ProvidedResource.of(Constants.Resources.SYNC, injector.getInstance(SyncService.class))
		));
		if (isNetworkInstalled()) resources.addAll(ChannelizerNetworkResources.provide(injector.getInstance(PlatformMessageBus.class)));

		return resources;
	}

	/**
	 * Returns whether SocialismusNetwork is installed, whose API this module reaches only then.
	 */
	private boolean isNetworkInstalled() {
		return modules.getModule(NETWORK).isPresent();
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
