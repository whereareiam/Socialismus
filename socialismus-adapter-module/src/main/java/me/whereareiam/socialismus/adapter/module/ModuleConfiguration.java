package me.whereareiam.socialismus.adapter.module;

import com.google.inject.AbstractModule;
import me.whereareiam.socialismus.adapter.module.provider.CacheServiceProvider;
import me.whereareiam.socialismus.adapter.module.provider.DatabaseServiceProvider;
import me.whereareiam.socialismus.adapter.module.provider.SyncServiceProvider;
import me.whereareiam.socialismus.api.output.module.ModuleService;
import me.whereareiam.socialismus.api.output.resource.CacheService;
import me.whereareiam.socialismus.api.output.resource.DatabaseService;
import me.whereareiam.socialismus.api.output.resource.ResourceRegistry;
import me.whereareiam.socialismus.api.output.resource.sync.SyncService;

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
