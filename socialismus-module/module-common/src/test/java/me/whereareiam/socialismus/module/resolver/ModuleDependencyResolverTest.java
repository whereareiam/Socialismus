package me.whereareiam.socialismus.module.resolver;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.module.ModuleService;
import me.whereareiam.socialismus.module.PlatformClassLoader;
import me.whereareiam.socialismus.module.model.InternalModule;
import me.whereareiam.socialismus.module.model.ModuleDependency;
import me.whereareiam.socialismus.module.model.PlatformDependency;
import me.whereareiam.socialismus.module.type.DependencyType;
import me.whereareiam.socialismus.module.type.ModuleState;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ModuleDependencyResolverTest {
	@BeforeAll
	static void initLogger() {
		Logger.init(mock(LoggingHelper.class));
	}

	@Test
	void acceptsAnEnabledPluginInAnAcceptedVersion() {
		ModuleDependencyResolver resolver = resolverFor(withPlugin("1.0.0"));

		assertTrue(resolver.resolve(module()));
	}

	@Test
	void refusesAPluginInAnotherVersion() {
		ModuleDependencyResolver resolver = resolverFor(withPlugin("2.0.0"));

		assertFalse(resolver.resolve(module()));
	}

	@Test
	void waitsWhenTheRequiredPluginIsNotEnabledYet() {
		ModuleDependencyResolver resolver = resolverFor(withoutPlugins());

		assertFalse(resolver.resolve(module()));
		assertTrue(resolver.isWaitingForPlatform(module()));
	}

	@Test
	void doesNotWaitForAnOptionalPlugin() {
		ModuleDependencyResolver resolver = resolverFor(withoutPlugins());
		InternalModule module = module().toBuilder()
				.dependencies(List.of(module().getDependencies().getFirst().toBuilder().optional(true).build()))
				.build();

		assertTrue(resolver.resolve(module));
		assertFalse(resolver.isWaitingForPlatform(module));
	}

	@Test
	void refusesAMissingIntegration() {
		ModuleDependencyResolver resolver = resolverFor(withoutPlugins());
		InternalModule module = InternalModule.builder()
				.name("Bubbler")
				.state(ModuleState.UNKNOWN)
				.dependencies(List.of(ModuleDependency.builder().name("PacketEvents").type(DependencyType.INTEGRATION).build()))
				.build();

		assertFalse(resolver.resolve(module));
	}

	@Test
	void acceptsAMissingOptionalModule() {
		ModuleDependencyResolver resolver = resolverFor(mock(PlatformClassLoader.class), List.of());

		assertTrue(resolver.resolve(dependingOn("SocialismusDatabase", true)));
	}

	@Test
	void refusesAMissingRequiredModule() {
		ModuleDependencyResolver resolver = resolverFor(mock(PlatformClassLoader.class), List.of());

		assertFalse(resolver.resolve(dependingOn("SocialismusDatabase", false)));
	}

	@Test
	void acceptsAModuleOfAnyVersionWhenNoneIsGiven() {
		InternalModule database = InternalModule.builder().name("SocialismusDatabase").version("dev").state(ModuleState.LOADED).build();
		ModuleDependencyResolver resolver = resolverFor(mock(PlatformClassLoader.class), List.of(database));

		assertTrue(resolver.resolve(dependingOn("SocialismusDatabase", false)));
	}

	private static InternalModule dependingOn(String name, boolean optional) {
		return InternalModule.builder()
				.name("Dialogue")
				.state(ModuleState.UNKNOWN)
				.dependencies(List.of(ModuleDependency.builder()
						.name(name)
						.type(DependencyType.MODULE)
						.optional(optional)
						.build()))
				.build();
	}

	private ModuleDependencyResolver resolverFor(PlatformClassLoader platformClassLoader) {
		return resolverFor(platformClassLoader, List.of());
	}

	private ModuleDependencyResolver resolverFor(PlatformClassLoader platformClassLoader, List<InternalModule> modules) {
		return new ModuleDependencyResolver(new ModuleService() {
			@Override
			public void loadModules() {
			}

			@Override
			public void loadPendingModules() {
			}

			@Override
			public void unloadModules() {
			}

			@Override
			public void reloadModules() {
			}

			@Override
			public List<InternalModule> getModules() {
				return modules;
			}

			@Override
			public Optional<InternalModule> getModule(String name) {
				return Optional.empty();
			}
		}, Set::of, platformClassLoader);
	}

	private InternalModule module() {
		return InternalModule.builder()
				.name("DummyClanModule")
				.state(ModuleState.UNKNOWN)
				.dependencies(List.of(ModuleDependency.builder()
						.name("DummyClans")
						.version("1\\.0\\.0")
						.type(DependencyType.PLATFORM)
						.build()))
				.build();
	}

	private static PlatformClassLoader withPlugin(String version) {
		return new PlatformClassLoader() {
			@Override
			public ClassLoader getClassLoader() {
				return getClass().getClassLoader();
			}

			@Override
			public @NotNull Optional<PlatformDependency> findDependency(@NotNull String name) {
				return Optional.of(new PlatformDependency(name, version, getClass().getClassLoader()));
			}
		};
	}

	private static PlatformClassLoader withoutPlugins() {
		return new PlatformClassLoader() {
			@Override
			public ClassLoader getClassLoader() {
				return getClass().getClassLoader();
			}
		};
	}
}
