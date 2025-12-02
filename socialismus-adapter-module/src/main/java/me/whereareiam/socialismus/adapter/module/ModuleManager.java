package me.whereareiam.socialismus.adapter.module;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.Getter;
import lombok.Setter;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.module.InternalModule;
import me.whereareiam.socialismus.model.module.Module;
import me.whereareiam.socialismus.model.module.ModuleDependency;
import me.whereareiam.socialismus.module.ModuleService;
import me.whereareiam.socialismus.type.module.DependencyType;
import me.whereareiam.socialismus.type.module.ModuleState;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@Singleton
public class ModuleManager implements ModuleService {
	private static final String MODULE_FILE = "module.json";

	private final Path modulesPath;
	private final ModuleLifecycleController lifecycleController;

	private List<InternalModule> modules = new ArrayList<>();

	@Inject
	public ModuleManager(
			@Named("modulesPath") Path modulesPath,
			ModuleLifecycleController moduleLifecycleController
	) {
		this.modulesPath = modulesPath;
		this.lifecycleController = moduleLifecycleController;
	}

	@Override
	public Optional<InternalModule> getModule(String name) {
		return modules.stream().filter(module -> module.getName().equals(name)).findFirst();
	}

	@Override
	public void loadModules() {
		discoverModules();
		modules.forEach(lifecycleController::loadModule);
		modules.forEach(lifecycleController::enableModule);
	}

	@Override
	public void unloadModules() {
		Logger.info("Unloading modules...");
		modules.forEach(lifecycleController::disableModule);
		modules.forEach(lifecycleController::unloadModule);

		modules.removeIf(module -> !module.getState().equals(ModuleState.UNLOADED));
		modules.forEach(module -> Logger.warn("Module was not unloaded: " + module.getName()));
	}

	@Override
	public void reloadModules() {
		unloadModules();
		loadModules();
	}

	private void discoverModules() {
		try (Stream<Path> paths = Files.list(modulesPath)) {
			List<File> moduleFiles = paths
					.filter(Files::isRegularFile)
					.filter(path -> path.getFileName().toString().endsWith(".jar"))
					.map(Path::toFile)
					.toList();

			moduleFiles.forEach(file -> {
				try (JarFile jarFile = new JarFile(file)) {
					JarEntry entry = jarFile.getJarEntry(MODULE_FILE);
					if (entry == null) {
						Logger.warn("Module file does not contain module.json file: " + file.getName());
						return;
					}

					try (InputStream stream = jarFile.getInputStream(entry)) {
						Module module = Config.load(stream, Module.class);
						if (!validateModule(module, file)) {
							return;
						}

						modules.add(InternalModule.builder()
								.path(file.toPath())
								.state(ModuleState.UNKNOWN)
								.name(module.getName())
								.version(module.getVersion())
								.authors(module.getAuthors())
								.supportedPlatforms(module.getSupportedPlatforms())
								.supportedVersions(module.getSupportedVersions())
								.dependencies(module.getDependencies())
								.updater(module.getUpdater())
								.main(module.getMain())
								.build());
					}
				} catch (Exception e) {
					Logger.warn("Failed to load module from file '" + file.getName() + "': " + e.getMessage());
					Logger.debug("Module loading stack trace:", e);
				}

				List<InternalModule> sortedModules = sortModulesByDependencies(modules);
				modules.clear();
				modules.addAll(sortedModules);
			});
		} catch (IOException e) {
			Logger.warn("Failed to load modules from directory: " + modulesPath);
		}
	}

	private List<InternalModule> sortModulesByDependencies(List<InternalModule> modules) {
		Map<String, InternalModule> moduleMap = modules.stream()
				.collect(Collectors.toMap(InternalModule::getName, module -> module));

		Map<String, List<String>> dependencyGraph = new HashMap<>();
		for (InternalModule module : modules) {
			List<String> dependencies = module.getDependencies().stream()
					.filter(dep -> dep.getType() == DependencyType.MODULE)
					.map(ModuleDependency::getName)
					.collect(Collectors.toList());
			dependencyGraph.put(module.getName(), dependencies);
		}

		List<InternalModule> sortedModules = new ArrayList<>();
		Set<String> visited = new HashSet<>();
		Set<String> visiting = new HashSet<>();

		for (InternalModule module : modules)
			if (!visited.contains(module.getName()))
				if (topologicalSort(module.getName(), dependencyGraph, visited, visiting, sortedModules, moduleMap)) {
					Logger.warn("Cyclic dependency detected in modules");
					return modules;
				}

		return sortedModules;
	}

	private boolean topologicalSort(String moduleName, Map<String, List<String>> dependencyGraph, Set<String> visited, Set<String> visiting, List<InternalModule> sortedModules, Map<String, InternalModule> moduleMap) {
		if (visiting.contains(moduleName)) return true;
		if (visited.contains(moduleName)) return false;

		visiting.add(moduleName);
		for (String dependency : dependencyGraph.getOrDefault(moduleName, Collections.emptyList()))
			if (topologicalSort(dependency, dependencyGraph, visited, visiting, sortedModules, moduleMap))
				return true;

		visiting.remove(moduleName);
		visited.add(moduleName);
		sortedModules.add(moduleMap.get(moduleName));

		return false;
	}

	private boolean validateModule(Module module, File moduleJson) {
		if (module == null) {
			Logger.warn("Failed to open module.json file: " + moduleJson.getName());
			return false;
		}

		if (module.getName() == null || module.getVersion() == null || module.getMain() == null) {
			Logger.warn("Module file is missing required fields: " + moduleJson.getName());
			return false;
		}

		if (modules.stream().anyMatch(internalModule -> internalModule.getName().equals(module.getName()))) {
			Logger.warn("Module with name already exists: " + module.getName());
			return false;
		}

		return true;
	}
}
