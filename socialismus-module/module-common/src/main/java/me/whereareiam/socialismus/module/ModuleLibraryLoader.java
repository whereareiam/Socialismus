package me.whereareiam.socialismus.module;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.attache.LibraryManager;
import me.whereareiam.attache.platform.standalone.StandaloneLibraryManager;
import me.whereareiam.socialismus.exception.ModuleLifecycleException;
import me.whereareiam.socialismus.module.model.InternalModule;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;

/**
 * Loads the libraries a module declares itself into the module's class loader.
 */
@Singleton
public class ModuleLibraryLoader {
	private final LibraryManager pluginLibraries;

	@Inject
	public ModuleLibraryLoader(@NotNull LibraryManager pluginLibraries) {
		this.pluginLibraries = pluginLibraries;
	}

	/**
	 * Loads the libraries the module declares outside any group into its class loader, reading only the module's
	 * own jar for them, and returns the module's access to its groups.
	 *
	 * @param module      the module
	 * @param classLoader the module's class loader
	 * @return the module's libraries; closed when the module unloads
	 * @throws ModuleLifecycleException when a library cannot be loaded
	 */
	@NotNull
	public LoadedModuleLibraries loadOwnLibraries(@NotNull InternalModule module, @NotNull URLClassLoader classLoader) {
		Path saveDirectory = pluginLibraries.getSaveDirectory();
		StandaloneLibraryManager libraries = new StandaloneLibraryManager(
				pluginLibraries.getLoggingHelper(),
				saveDirectory.getParent(),
				saveDirectory.getFileName().toString(),
				classLoader
		);

		try {
			libraries.setVerbosityMode(pluginLibraries.getVerbosityMode());
			pluginLibraries.getRepositories().forEach(libraries::addRepository);
			libraries.setDescriptorSources(List.of(module.getPath().toUri().toURL()));
			libraries.loadDescriptors();
		} catch (MalformedURLException | RuntimeException failure) {
			close(libraries);
			throw new ModuleLifecycleException("Module " + module.getName() + " could not load its libraries: "
					+ failure.getMessage(), failure);
		}

		return new LoadedModuleLibraries(module.getName(), libraries);
	}

	private static void close(StandaloneLibraryManager libraries) {
		try {
			libraries.close();
		} catch (Exception ignored) {
			// Nothing was loaded through it that needs releasing.
		}
	}

	/**
	 * A module's libraries, which load its groups on demand.
	 */
	public static final class LoadedModuleLibraries implements ModuleLibraries, AutoCloseable {
		private final String moduleName;
		private final StandaloneLibraryManager libraries;

		private LoadedModuleLibraries(String moduleName, StandaloneLibraryManager libraries) {
			this.moduleName = moduleName;
			this.libraries = libraries;
		}

		@Override
		public void loadGroups(@NotNull String... groups) {
			try {
				libraries.loadGroups(groups);
			} catch (IllegalArgumentException failure) {
				// An undeclared group is the module's own mistake; Attache names the groups it does declare.
				throw failure;
			} catch (RuntimeException failure) {
				throw new ModuleLifecycleException("Module " + moduleName + " could not load the library group(s) "
						+ String.join(", ", groups) + ": " + failure.getMessage(), failure);
			}
		}

		@Override
		public void close() throws Exception {
			libraries.close();
		}
	}
}
