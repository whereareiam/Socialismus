package me.whereareiam.socialismus.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import me.whereareiam.chassis.ExtensionTransformer;
import me.whereareiam.chassis.Extensions;
import me.whereareiam.chassis.common.ChassisHost;
import me.whereareiam.socialismus.lifecycle.RuntimeLifecycle;
import me.whereareiam.socialismus.registry.ResourceRegistry;

/**
 * Puts the modules together: one host of them, which the rest of Socialismus sees as {@link Extensions}, started in
 * the modules' phase, and the registry of the resources modules provide.
 */
public class ModuleConfiguration extends AbstractModule {
	@Override
	protected void configure() {
		Multibinder.newSetBinder(binder(), RuntimeLifecycle.class).addBinding().to(ModuleLifecycle.class);
		bind(ResourceRegistry.class).to(ResourceRegistryAdapter.class);
		// What a platform changes in module jars before they load, such as the BUKKIT jar's relocated Adventure.
		Multibinder.newSetBinder(binder(), ExtensionTransformer.class);
	}

	@Provides
	@Singleton
	ChassisHost<ModuleContext, ModuleFields> modules(ModuleHostFactory factory) {
		return factory.create();
	}

	@Provides
	Extensions extensions(ChassisHost<ModuleContext, ModuleFields> modules) {
		return modules;
	}
}
