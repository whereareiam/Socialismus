package me.whereareiam.socialismus.adapter.module;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.adapter.module.resolver.ModuleDependencyResolver;
import me.whereareiam.socialismus.adapter.module.resolver.ModulePlatformResolver;
import me.whereareiam.socialismus.adapter.module.resolver.ModuleResolver;
import me.whereareiam.socialismus.adapter.module.resolver.ModuleVersionResolver;
import me.whereareiam.socialismus.api.AnsiColor;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.model.module.InternalModule;
import me.whereareiam.socialismus.api.output.PlatformClassLoader;
import me.whereareiam.socialismus.api.output.module.SocialisticModule;
import me.whereareiam.socialismus.api.type.module.ModuleState;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

@Singleton
public class ModuleLifecycleController {
	private final Injector injector;
	private final PlatformClassLoader platformClassLoader;

	private final List<ModuleResolver> resolvers;

	@Inject
	public ModuleLifecycleController(Injector injector, PlatformClassLoader platformClassLoader) {
		this.injector = injector;
		this.platformClassLoader = platformClassLoader;

		this.resolvers = List.of(
				injector.getInstance(ModuleDependencyResolver.class),
				injector.getInstance(ModuleVersionResolver.class),
				injector.getInstance(ModulePlatformResolver.class)
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

			module.getModule().onLoad();
		} catch (MalformedURLException | ClassNotFoundException e) {
			Logger.severe("Failed to load module " + module.getName() + ": " + e);
			module.setState(ModuleState.ERROR);
		}
	}

	public void enableModule(InternalModule module) {
		if (!module.getState().equals(ModuleState.LOADED)) return;

		module.setState(ModuleState.ENABLED);
		module.getModule().onEnable();
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
