package me.whereareiam.socialismus.module;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.AnsiColor;
import me.whereareiam.socialismus.exception.ModuleLifecycleException;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.model.InternalModule;
import me.whereareiam.socialismus.module.model.ModuleDependency;
import me.whereareiam.socialismus.module.resolver.*;
import me.whereareiam.socialismus.module.type.DependencyType;
import me.whereareiam.socialismus.module.type.ModuleState;
import me.whereareiam.socialismus.registry.ResourceRegistry;
import me.whereareiam.socialismus.service.resource.ResourceProvider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class ModuleLifecycleController {
	private final Injector injector;
	private final PlatformClassLoader platformClassLoader;
	private final ResourceRegistry registry;
	private final Map<InternalModule, ModuleClassLoader> moduleClassLoaders = new IdentityHashMap<>();

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
		if (module.getState() != ModuleState.UNKNOWN && module.getState() != ModuleState.WAITING_FOR_DEPENDENCY) return;
		if (checkRequirements(module)) return;

		ModuleClassLoader loader = null;
		try {
			loader = createModuleClassLoader(module);
			Class<?> moduleClass = Class.forName(module.getMain(), true, loader);

			module.setModule((SocialisticModule) injector.getInstance(moduleClass));
			moduleClassLoaders.put(module, loader);

			module.setState(ModuleState.LOADED);
			module.getModule().setModule(module);
			module.getModule().setWorkingPath(module.getPath().getParent().resolve(module.getName()));

			injector.injectMembers(module.getModule());

			try {
				module.getModule().onLoad();

				if (module.getModule() instanceof ResourceProvider provider) {
					provider.provideResources().forEach((k, v) -> {
						registry.register(k, v);
						Logger.info("Registered resource " + k + " from module " + module.getName());
					});
				}
			} catch (ModuleLifecycleException | ProvisionException ex) {
				closeModuleClassLoader(moduleClassLoaders.remove(module));
				Logger.severe("Module " + module.getName() + " aborted load: " + ex.getMessage());
				module.setState(ModuleState.ERROR);
				return;
			}

			Logger.info("Loaded module " + AnsiColor.YELLOW + module.getName() + AnsiColor.RESET + " v" + module.getVersion() + " [" + String.join(", ", module.getAuthors()) + "]");
		} catch (MalformedURLException | ClassNotFoundException | ModuleLifecycleException | ProvisionException | LinkageError e) {
			ModuleClassLoader storedLoader = moduleClassLoaders.remove(module);
			if (storedLoader != loader) closeModuleClassLoader(storedLoader);
			closeModuleClassLoader(loader);
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

		closeModuleClassLoader(moduleClassLoaders.remove(module));
	}

	private ModuleClassLoader createModuleClassLoader(InternalModule module) throws MalformedURLException {
		Map<String, ClassLoader> externalApiClassLoaders = new HashMap<>();
		for (ModuleDependency dependency : module.getDependencies()) {
			if (dependency.getType() != DependencyType.PLATFORM_COMPONENT) continue;

			ClassLoader dependencyClassLoader = platformClassLoader.findDependency(dependency.getName())
					.orElseThrow(() -> new ModuleLifecycleException("Platform component " + dependency.getName() + " is no longer available"))
					.getClassLoader();
			dependency.getApiPackages().forEach(apiPackage -> externalApiClassLoaders.put(apiPackage, dependencyClassLoader));
		}

		return new ModuleClassLoader(module.getPath().toUri().toURL(), platformClassLoader.getClassLoader(), externalApiClassLoaders);
	}

	private void closeModuleClassLoader(ModuleClassLoader loader) {
		if (loader == null) return;

		try {
			loader.close();
		} catch (IOException exception) {
			Logger.warn("Failed to close module class loader: " + exception.getMessage());
		}
	}

	private boolean checkRequirements(InternalModule module) {
		for (ModuleResolver resolver : resolvers) {
			if (!resolver.resolve(module)) {
				if (resolver instanceof ModuleDependencyResolver dependencyResolver
						&& dependencyResolver.isWaitingForPlatformComponent(module)) {
					module.setState(ModuleState.WAITING_FOR_DEPENDENCY);
					return true;
				}

				module.setState(ModuleState.ERROR);
				return true;
			}
		}

		return false;
	}
}
