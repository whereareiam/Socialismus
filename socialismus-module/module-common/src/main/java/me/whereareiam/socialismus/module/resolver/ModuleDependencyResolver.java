package me.whereareiam.socialismus.module.resolver;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.integration.Integration;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.ModuleService;
import me.whereareiam.socialismus.module.PlatformClassLoader;
import me.whereareiam.socialismus.module.model.InternalModule;
import me.whereareiam.socialismus.module.model.ModuleDependency;
import me.whereareiam.socialismus.module.model.PlatformDependency;
import me.whereareiam.socialismus.module.type.DependencyType;
import me.whereareiam.socialismus.module.type.ModuleState;

import java.util.Set;
import java.util.regex.Pattern;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ModuleDependencyResolver implements ModuleResolver {
	private final ModuleService moduleService;
	private final Provider<Set<Integration>> integrations;
	private final PlatformClassLoader platformClassLoader;

	@Override
	public boolean resolve(InternalModule module) {
		if (module.getDependencies().isEmpty()) return true;

		for (ModuleDependency dependency : module.getDependencies()) {
			DependencyType type = dependency.getType();
			String name = dependency.getName();
			String version = dependency.getVersion();

			switch (type) {
				case INTEGRATION:
					if (!isIntegrationAvailable(name)) {
						Logger.warn("Module %s requires integration %s, which is not available", module.getName(), name);
						return false;
					}
					break;
				case PLATFORM_COMPONENT:
					if (!isPlatformComponentAvailable(dependency)) {
						Logger.warn("Module %s requires platform component %s with version %s, which is not available", module.getName(), name, version);
						return false;
					}
					break;
				case MODULE:
					if (!isModuleAvailable(name, version)) {
						Logger.warn("Module %s requires module %s with version %s, which is not available", module.getName(), name, version);
						return false;
					}
					break;
				case BASE:
					if (!isBaseVersionCompatible(version)) {
						Logger.warn("Module %s requires version of the plugin to be %s, but it is %s", module.getName(), version, Constants.VERSION);
						return false;
					}
					break;
			}
		}

		return true;
	}

	private boolean isIntegrationAvailable(String name) {
		return integrations.get().stream().anyMatch(integration -> integration.getName().equals(name));
	}

	private boolean isModuleAvailable(String name, String version) {
		Pattern versionPattern = Pattern.compile(version);
		return moduleService.getModules().stream()
				.anyMatch(module -> module.getName().equals(name) &&
						versionPattern.matcher(module.getVersion()).matches() &&
						module.getState() != ModuleState.ERROR);
	}

	private boolean isBaseVersionCompatible(String version) {
		Pattern versionPattern = Pattern.compile(version);

		return versionPattern.matcher(Constants.VERSION).matches();
	}

	private boolean isPlatformComponentAvailable(ModuleDependency dependency) {
		if (!hasValidPlatformComponentMetadata(dependency)) return false;

		return platformClassLoader.findDependency(dependency.getName())
				.map(platformDependency -> isVersionCompatible(platformDependency, dependency.getVersion()))
				.orElse(false);
	}

	private boolean isVersionCompatible(PlatformDependency dependency, String requiredVersion) {
		if (requiredVersion == null || requiredVersion.isBlank()) return true;

		return Pattern.compile(requiredVersion).matcher(dependency.getVersion()).matches();
	}

	public boolean isWaitingForPlatformComponent(InternalModule module) {
		return module.getDependencies().stream()
				.filter(dependency -> dependency.getType() == DependencyType.PLATFORM_COMPONENT)
				.anyMatch(dependency -> hasValidPlatformComponentMetadata(dependency)
						&& platformClassLoader.findDependency(dependency.getName()).isEmpty());
	}

	private boolean hasValidPlatformComponentMetadata(ModuleDependency dependency) {
		if (dependency.getName() == null || dependency.getName().isBlank()) return false;
		if (dependency.getApiPackages() == null || dependency.getApiPackages().isEmpty()) return false;

		return dependency.getApiPackages().stream()
				.noneMatch(apiPackage -> apiPackage == null || apiPackage.isBlank() || !apiPackage.endsWith("."));
	}
}
