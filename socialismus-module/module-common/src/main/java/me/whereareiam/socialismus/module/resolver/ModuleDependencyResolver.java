package me.whereareiam.socialismus.module.resolver;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.ModuleService;
import me.whereareiam.socialismus.module.PlatformClassLoader;
import me.whereareiam.socialismus.module.model.InternalModule;
import me.whereareiam.socialismus.module.model.ModuleDependency;
import me.whereareiam.socialismus.module.type.DependencyType;
import me.whereareiam.socialismus.module.type.ModuleState;

import java.util.Set;

/**
 * Accepts a module whose dependencies are there, except those it marks optional.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ModuleDependencyResolver implements ModuleResolver {
	private final ModuleService moduleService;
	private final Provider<Set<Integration>> integrations;
	private final PlatformClassLoader platformClassLoader;

	@Override
	public boolean resolve(InternalModule module) {
		for (ModuleDependency dependency : module.getDependencies()) {
			if (dependency.isOptional() || isAvailable(dependency)) continue;

			Logger.warn("Module %s requires %s %s%s, which is not available",
					module.getName(),
					describe(dependency.getType()),
					dependency.getName(),
					dependency.getVersion() == null ? "" : " with version " + dependency.getVersion()
			);
			return false;
		}

		return true;
	}

	/**
	 * Returns whether the module waits for a plugin the platform may still enable: a dependency on the platform that
	 * is not optional and not there yet.
	 *
	 * @param module the module
	 * @return whether the module is loaded again once the platform enables more plugins
	 */
	public boolean isWaitingForPlatform(InternalModule module) {
		return module.getDependencies().stream()
				.filter(dependency -> dependency.getType() == DependencyType.PLATFORM && !dependency.isOptional())
				.anyMatch(dependency -> platformClassLoader.findDependency(dependency.getName()).isEmpty());
	}

	private boolean isAvailable(ModuleDependency dependency) {
		String name = dependency.getName();
		if (name == null || name.isBlank()) return false;

		return switch (dependency.getType()) {
			case MODULE -> isModuleAvailable(name, dependency.getVersion());
			case INTEGRATION -> integrations.get().stream().anyMatch(integration -> integration.getName().equals(name));
			case PLATFORM -> platformClassLoader.findDependency(name)
					.map(platform -> VersionPattern.accepts(dependency.getVersion(), platform.getVersion()))
					.orElse(false);
		};
	}

	private boolean isModuleAvailable(String name, String version) {
		return moduleService.getModules().stream()
				.anyMatch(module -> module.getName().equals(name)
						&& VersionPattern.accepts(version, module.getVersion())
						&& module.getState() != ModuleState.ERROR);
	}

	private static String describe(DependencyType type) {
		return switch (type) {
			case MODULE -> "module";
			case INTEGRATION -> "integration";
			case PLATFORM -> "plugin";
		};
	}
}
