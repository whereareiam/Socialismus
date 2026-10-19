package me.whereareiam.socialismus.module;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import me.whereareiam.socialismus.lifecycle.RuntimeLifecycle;
import me.whereareiam.socialismus.registry.ResourceRegistry;

public class ModuleConfiguration extends AbstractModule {
	@Override
	protected void configure() {
		bind(ModuleService.class).to(ModuleManager.class);
		Multibinder.newSetBinder(binder(), RuntimeLifecycle.class).addBinding().to(ModuleLifecycle.class);
		bind(ResourceRegistry.class).to(ResourceRegistryAdapter.class);
	}
}
