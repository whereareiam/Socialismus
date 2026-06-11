package me.whereareiam.socialismus.module;

import com.google.inject.AbstractModule;
import me.whereareiam.socialismus.module.provider.DatabaseServiceProvider;
import me.whereareiam.socialismus.module.provider.cache.CacheServiceProvider;
import me.whereareiam.socialismus.module.provider.sync.SyncServiceProvider;
import me.whereareiam.socialismus.registry.ResourceRegistry;
import me.whereareiam.socialismus.service.resource.CacheService;
import me.whereareiam.socialismus.service.resource.DatabaseService;
import me.whereareiam.socialismus.service.resource.sync.SyncService;

public class ModuleConfiguration extends AbstractModule {
	@Override
	protected void configure() {
		bind(ModuleService.class).to(ModuleManager.class);
		bind(ResourceRegistry.class).to(ResourceRegistryAdapter.class);

		bind(SyncService.class).toProvider(SyncServiceProvider.class);
		bind(CacheService.class).toProvider(CacheServiceProvider.class);
		bind(DatabaseService.class).toProvider(DatabaseServiceProvider.class);
	}
}
