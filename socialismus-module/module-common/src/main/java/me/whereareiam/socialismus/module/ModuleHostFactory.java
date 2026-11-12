package me.whereareiam.socialismus.module;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import me.whereareiam.attache.LibraryManager;
import me.whereareiam.chassis.ExtensionContext;
import me.whereareiam.chassis.ExtensionFactory;
import me.whereareiam.chassis.adapter.attache.AttacheLibraries;
import me.whereareiam.chassis.common.ChassisHost;
import me.whereareiam.chassis.common.HostOptions;
import me.whereareiam.chassis.common.loading.ExportLayer;
import me.whereareiam.chassis.model.Version;
import me.whereareiam.chassis.platform.Platform;
import me.whereareiam.socialismus.BuildConfig;
import me.whereareiam.socialismus.integration.Integration;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Makes Socialismus's modules: the jars of {@code modules/}, each described by its {@code module.yml}, loaded in a
 * class loader of its own that sees Socialismus's API, the libraries it provides and the server's API, and made
 * through Socialismus's injector.
 */
public final class ModuleHostFactory {
	// What a module sees of Socialismus's class loader. Guice, Jackson and Guava are relocated below
	// me.whereareiam.socialismus.
	private static final List<String> EXPORTS = List.of(
			"me.whereareiam.socialismus",
			"me.whereareiam.chassis",
			"me.whereareiam.configura",
			"me.whereareiam.strata",
			"me.whereareiam.keystone",
			"me.whereareiam.commandant",
			"net.kyori",
			"org.incendo.cloud",
			"org.jetbrains.annotations",
			"org.slf4j",
			"com.github.retrooper",
			"io.github.retrooper",
			"org.bukkit",
			"org.spigotmc",
			"io.papermc",
			"com.destroystokyo",
			"com.velocitypowered"
	);

	private final Injector injector;
	private final Platform platform;
	private final LibraryManager libraries;
	// The integrations that started, which the integrations' phase fills before the modules' phase.
	private final Provider<Set<Integration>> integrations;
	private final Path modulesPath;
	private final Path dataPath;

	@Inject
	public ModuleHostFactory(
			@NotNull Injector injector,
			@NotNull Platform platform,
			@NotNull LibraryManager libraries,
			@NotNull Provider<Set<Integration>> integrations,
			@Named("modulesPath") @NotNull Path modulesPath,
			@Named("dataPath") @NotNull Path dataPath
	) {
		this.injector = injector;
		this.platform = platform;
		this.libraries = libraries;
		this.integrations = integrations;
		this.modulesPath = modulesPath;
		this.dataPath = dataPath;
	}

	/**
	 * Makes the modules; nothing is read until they start.
	 *
	 * @return the modules
	 */
	public @NotNull ChassisHost<ModuleContext, ModuleFields> create() {
		AttacheLibraries attache = AttacheLibraries.builder()
				.folder(libraries.getSaveDirectory())
				.logging(libraries.getLoggingHelper())
				.verbosity(libraries.getVerbosityMode())
				.repositories(libraries.getRepositories())
				.build();

		return new ChassisHost<>(options(attache), SocialisticModule.class, ModuleFields.class,
				(base, fields) -> new DefaultModuleContext(base, attache.groups(base.descriptor().getId())), factory());
	}

	private HostOptions options(AttacheLibraries attache) {
		HostOptions.HostOptionsBuilder options = HostOptions.builder()
				.kind("module")
				.folder(modulesPath)
				.descriptorFile("module.yml")
				.dataFolder(modulesPath)
				.selectionFile(dataPath.resolve("modules.yml"))
				.hostVersion(Version.parse(BuildConfig.HOST_VERSION))
				.platform(platform)
				.exportLayer(new ExportLayer(SocialisticModule.class.getClassLoader(), EXPORTS))
				.preparation(attache)
				.loggers(ModuleLogger::new);

		// A module requires an integration that started by its name in lower case, in Socialismus's version.
		Version version = Version.parse(BuildConfig.HOST_VERSION);
		for (Integration integration : integrations.get())
			options.offer(integration.getName().toLowerCase(Locale.ROOT), version);

		return options.build();
	}

	// A module's main class is made by Socialismus's injector, so its constructor takes Socialismus's services.
	private ExtensionFactory factory() {
		return new ExtensionFactory() {
			@Override
			public <T> @NotNull T create(@NotNull Class<T> main, @NotNull ExtensionContext context) {
				return injector.getInstance(main);
			}
		};
	}
}
