package me.whereareiam.socialismus.module.channelizer;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.model.resource.ProvidedResource;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.module.channelizer.platform.bukkit.BukkitInjectorConfiguration;
import me.whereareiam.socialismus.module.channelizer.platform.bukkit.CommonConfiguration;
import me.whereareiam.socialismus.service.resource.ResourceProvider;
import me.whereareiam.socialismus.service.resource.sync.SyncService;
import me.whereareiam.socialismus.type.PlatformType;

import java.util.List;

@SuppressWarnings("unused")
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SocialismusChannelizer extends SocialisticModule implements ResourceProvider {
	private final Injector parentInjector;
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
		return List.of(
				ProvidedResource.of(Constants.Resources.SYNC, injector.getInstance(SyncService.class))
		);
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
