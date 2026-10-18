package me.whereareiam.socialismus.module.redis;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.RequiredArgsConstructor;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.model.resource.ProvidedResource;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.module.redis.common.CommonConfiguration;
import me.whereareiam.socialismus.module.redis.common.config.migration.RedisConfigMigrations;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.resource.CacheService;
import me.whereareiam.socialismus.service.resource.ResourceProvider;
import me.whereareiam.socialismus.service.resource.sync.SyncService;
import me.whereareiam.strata.Strata;

import java.util.List;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SocialismusRedis extends SocialisticModule implements ResourceProvider {
	private final Registry<Reloadable> reloadableRegistry;
	private final EventManager eventManager;
	private Injector injector;

	@Override
	public void onLoad() {
		new Strata(List.of(RedisConfigMigrations.on(Config.configured(), workingPath))).migrate();

		injector =
				Guice.createInjector(
						new SocialismusRedisInjectorConfiguration(
								reloadableRegistry,
								eventManager
						),
						new CommonConfiguration(workingPath));
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
				ProvidedResource.of(Constants.Resources.SYNC, injector.getInstance(SyncService.class)),
				ProvidedResource.of(Constants.Resources.CACHE, injector.getInstance(CacheService.class))
		);
	}
}
