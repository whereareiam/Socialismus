package me.whereareiam.socialismus.adapter.module;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.adapter.module.resolver.*;
import me.whereareiam.socialismus.api.AnsiColor;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.exception.ModuleLifecycleException;
import me.whereareiam.socialismus.api.model.module.InternalModule;
import me.whereareiam.socialismus.api.output.PlatformClassLoader;
import me.whereareiam.socialismus.api.output.module.SocialisticModule;
import me.whereareiam.socialismus.api.output.resource.ResourceProvider;
import me.whereareiam.socialismus.api.output.resource.ResourceRegistry;
import me.whereareiam.socialismus.api.type.module.ModuleState;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

@Singleton
public class ModuleLifecycleController {
	private final Injector injector;
	private final PlatformClassLoader platformClassLoader;
	private final ResourceRegistry registry;

	private final List<ModuleResolver> resolvers;

	@Inject
	public ModuleLifecycleController(
			Injector injector,
			PlatformClassLoader platformClassLoader,
			ResourceRegistry registry
	) {
		this.injector = injector;
		this.platformClassLoader = platformClassLoader;
		this.registry = registry;

		this.resolvers = List.of(
				injector.getInstance(ModuleDependencyResolver.class),
				injector.getInstance(ModuleVersionResolver.class),
				injector.getInstance(ModulePlatformResolver.class),
				injector.getInstance(ModuleResourceResolver.class)
		);
	}

	public void loadModule(InternalModule module) {
		if (!module.getState().equals(ModuleState.UNKNOWN)) return;

		try {
			URLClassLoader loader = new URLClassLoader(new URL[]{module.getPath().toUri().toURL()}, platformClassLoader.getClassLoader());
			Class<?> moduleClass = Class.forName(module.getMain(), true, loader);

			module.setModule((SocialisticModule) injector.getInstance(moduleClass));

			module.setState(ModuleState.LOADED);
			module.getModule().setModule(module);
			module.getModule().setWorkingPath(module.getPath().getParent().resolve(module.getName()));

			injector.injectMembers(module.getModule());

			if (checkRequirements(module)) return;

			Logger.info("Loaded module " + AnsiColor.YELLOW + module.getName() + AnsiColor.RESET + " v" + module.getVersion() + " [" + String.join(", ", module.getAuthors()) + "]");

			try {
				module.getModule().onLoad();
			} catch (ModuleLifecycleException ex) {
				Logger.severe("Module " + module.getName() + " aborted load: " + ex.getMessage());
				module.setState(ModuleState.ERROR);
				return;
			}

			if (module.getModule() instanceof ResourceProvider provider) {
				provider.provideResources().forEach((k, v) -> {
					registry.register(k, v);
					Logger.info("Registered resource " + k + " from module " + module.getName());
				});
			}
		} catch (MalformedURLException | ClassNotFoundException e) {
			Logger.severe("Failed to load module " + module.getName() + ": " + e);
			module.setState(ModuleState.ERROR);
		}
	}

	public void enableModule(InternalModule module) {
		if (!module.getState().equals(ModuleState.LOADED)) return;

		module.setState(ModuleState.ENABLED);

		try {
			module.getModule().onEnable();
		} catch (ModuleLifecycleException ex) {
			Logger.severe("Module " + module.getName() + " aborted enable: " + ex.getMessage());
			module.setState(ModuleState.ERROR);
		}
	}

	public void disableModule(InternalModule module) {
		if (!module.getState().equals(ModuleState.ENABLED)) return;

		module.setState(ModuleState.DISABLED);
		module.getModule().onDisable();
	}

	public void unloadModule(InternalModule module) {
		if (!module.getState().equals(ModuleState.DISABLED)) return;

		module.setState(ModuleState.UNLOADED);
		module.getModule().onUnload();

		if (module.getModule() instanceof ResourceProvider provider) {
			provider.provideResources().keySet().forEach(registry::unregister);
		}
	}

	private boolean checkRequirements(InternalModule module) {
		for (ModuleResolver resolver : resolvers) {
			if (!resolver.resolve(module)) {
				module.setState(ModuleState.ERROR);
				return true;
			}
		}

		return false;
	}
}
