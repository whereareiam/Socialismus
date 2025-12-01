package me.whereareiam.socialismus.adapter.module.resolver;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.Logger;
import me.whereareiam.socialismus.model.module.InternalModule;
import me.whereareiam.socialismus.model.module.ModuleDependency;
import me.whereareiam.socialismus.output.integration.Integration;
import me.whereareiam.socialismus.output.module.ModuleService;
import me.whereareiam.socialismus.type.module.DependencyType;
import me.whereareiam.socialismus.type.module.ModuleState;

import java.util.Set;
import java.util.regex.Pattern;

@Singleton
public class ModuleDependencyResolver implements ModuleResolver {
	private final ModuleService moduleService;
	private final Provider<Set<Integration>> integrations;

	@Inject
	public ModuleDependencyResolver(ModuleService moduleService, Provider<Set<Integration>> integrations) {
		this.moduleService = moduleService;
		this.integrations = integrations;
	}

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
}