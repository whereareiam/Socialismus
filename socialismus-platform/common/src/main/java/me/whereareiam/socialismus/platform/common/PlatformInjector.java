package me.whereareiam.socialismus.platform.common;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import me.whereareiam.attache.LibraryManager;
import me.whereareiam.socialismus.command.CommandConfiguration;
import me.whereareiam.socialismus.common.CommonConfiguration;
import me.whereareiam.socialismus.common.CommonInjector;
import me.whereareiam.socialismus.module.ModuleConfiguration;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Starts the injector of Socialismus on a platform: the configuration every platform shares, joined by the
 * platform's own.
 */
public final class PlatformInjector {
	private PlatformInjector() {
	}

	/**
	 * Creates the injector and makes it the one {@link CommonInjector} hands out.
	 *
	 * @param dataPath        the directory Socialismus keeps its files in
	 * @param libraries       the platform's library manager, which loaded Socialismus's libraries and loads the
	 *                        library groups modules ask for
	 * @param platformModules the bindings of the platform, such as its scheduler and listeners
	 * @return the created injector
	 */
	@NotNull
	public static Injector create(@NotNull Path dataPath, @NotNull LibraryManager libraries, @NotNull Module... platformModules) {
		Module libraryBinding = binder -> binder.bind(LibraryManager.class).toInstance(libraries);
		List<Module> modules = Stream.concat(
				Stream.of(platformModules),
				Stream.of(
						libraryBinding,
						new PlatformConfiguration(),
						new CommonConfiguration(dataPath),
						new CommandConfiguration(),
						new ModuleConfiguration()
				)
		).toList();

		Injector injector = Guice.createInjector(modules);
		CommonInjector.setInjector(injector);

		return injector;
	}
}
