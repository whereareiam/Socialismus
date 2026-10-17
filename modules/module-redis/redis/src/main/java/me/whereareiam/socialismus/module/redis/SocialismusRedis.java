package me.whereareiam.socialismus.module.redis;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.module.redis.common.CommonConfiguration;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.resource.CacheService;
import me.whereareiam.socialismus.service.resource.ResourceProvider;
import me.whereareiam.socialismus.service.resource.sync.SyncService;
import me.whereareiam.socialismus.type.ResourceType;

import java.util.Map;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SocialismusRedis extends SocialisticModule implements ResourceProvider {
	private final Registry<Reloadable> reloadableRegistry;
	private Injector injector;

	@Override
	public void onLoad() {
		injector =
				Guice.createInjector(
						new SocialismusRedisInjectorConfiguration(
								reloadableRegistry
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
	public Map<ResourceType, ?> provideResources() {
		return Map.of(
				ResourceType.SYNC, injector.getInstance(SyncService.class),
				ResourceType.CACHE, injector.getInstance(CacheService.class)
		);
	}
}
