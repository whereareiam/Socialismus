package me.whereareiam.socialismus.module.resolver;

import me.whereareiam.socialismus.module.ModuleService;
import me.whereareiam.socialismus.module.PlatformClassLoader;
import me.whereareiam.socialismus.module.model.InternalModule;
import me.whereareiam.socialismus.module.model.ModuleDependency;
import me.whereareiam.socialismus.module.model.PlatformDependency;
import me.whereareiam.socialismus.module.type.DependencyType;
import me.whereareiam.socialismus.module.type.ModuleState;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ModuleDependencyResolverTest {
	@Test
	void acceptsAnEnabledPlatformComponentWithTheDeclaredApiPackages() {
		ModuleDependencyResolver resolver = resolverFor(new PlatformClassLoader() {
			@Override
			public ClassLoader getClassLoader() {
				return getClass().getClassLoader();
			}

			@Override
			public @NotNull Optional<PlatformDependency> findDependency(@NotNull String name) {
				return Optional.of(new PlatformDependency(name, "1.0.0", getClass().getClassLoader()));
			}
		});

		assertTrue(resolver.resolve(module()));
	}

	@Test
	void waitsWhenTheDeclaredPlatformComponentIsNotEnabledYet() {
		ModuleDependencyResolver resolver = resolverFor(new PlatformClassLoader() {
			@Override
			public ClassLoader getClassLoader() {
				return getClass().getClassLoader();
			}
		});

		assertTrue(resolver.isWaitingForPlatformComponent(module()));
	}

	private ModuleDependencyResolver resolverFor(PlatformClassLoader platformClassLoader) {
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
				return List.of();
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
						.type(DependencyType.PLATFORM_COMPONENT)
						.apiPackages(List.of("dev.example.clans.api."))
						.build()))
				.build();
	}
}
