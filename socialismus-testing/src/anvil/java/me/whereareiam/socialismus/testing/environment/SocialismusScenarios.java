package me.whereareiam.socialismus.testing.environment;

import me.whereareiam.anvil.api.model.process.MinecraftProxy;
import me.whereareiam.anvil.api.model.process.MinecraftServer;
import me.whereareiam.anvil.api.model.scenario.AnvilScenario;
import me.whereareiam.anvil.api.model.workspace.AssetSource;
import me.whereareiam.anvil.api.model.workspace.WorkspaceAsset;
import me.whereareiam.anvil.api.model.workspace.WorkspaceCache;
import me.whereareiam.anvil.api.model.workspace.WorkspacePlan;
import me.whereareiam.anvil.api.type.AssetInstallMode;
import me.whereareiam.anvil.api.type.CacheIdentity;
import me.whereareiam.anvil.integration.junit.AnvilScenarioFactory;
import me.whereareiam.anvil.integration.junit.ScenarioResources;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Builds the network a {@link Socialismus} declaration describes.
 */
public final class SocialismusScenarios implements AnvilScenarioFactory<Socialismus> {
	@Override
	public @NotNull AnvilScenario create(@NotNull Socialismus socialismus, @NotNull ScenarioResources resources) {
		Network network = socialismus.network();
		ServerFiles files = new ServerFiles();
		for (Class<? extends Consumer<ServerFiles>> change : socialismus.configure())
			instantiate(change).accept(files);

		AnvilScenario.AnvilScenarioBuilder scenario = AnvilScenario.builder().name(name(socialismus));
		for (Container container : socialismus.containers())
			scenario.container(container.process());

		for (String backend : network.backends()) {
			MinecraftServer.MinecraftServerBuilder server = network.server(backend);
			if (network.socialismus().contains(backend)) server.workspace(workspace(network, backend, socialismus, files));
			scenario.server(server.build());
		}
		if (!network.proxied()) return scenario.entrypoint(network.entryServer()).build();

		MinecraftProxy.MinecraftProxyBuilder proxy = network.proxy();
		if (network.socialismus().contains(Network.PROXY)) {
			ServerFiles proxyFiles = new ServerFiles();
			for (Class<? extends Consumer<ServerFiles>> change : socialismus.configure())
				instantiate(change).accept(proxyFiles);
			for (Class<? extends Consumer<ServerFiles>> change : socialismus.proxyConfigure())
				instantiate(change).accept(proxyFiles);
			proxy.workspace(workspace(network, Network.PROXY, socialismus, proxyFiles));
		}

		return scenario.entrypoint(Network.PROXY).proxy(proxy.build()).build();
	}

	private static WorkspacePlan workspace(Network network, String process, Socialismus socialismus, ServerFiles files) {
		String data = network.data(process);
		WorkspacePlan.WorkspacePlanBuilder workspace = WorkspacePlan.builder()
				.asset(WorkspaceAsset.builder()
						.group("socialismus")
						.source(AssetSource.artifact(network.artifact(process, socialismus.bundle())))
						.target(Path.of("plugins", "Socialismus.jar"))
						.build())
				// Socialismus downloads its libraries on first start and checks them itself, so a rebuilt plugin
				// keeps them.
				.cache(WorkspaceCache.builder()
						.group("socialismus-libraries")
						.path(Path.of(data, ".libraries"))
						.identity(CacheIdentity.PROCESS)
						.build());

		for (Plugin plugin : socialismus.plugins())
			workspace.asset(WorkspaceAsset.builder()
					.group("plugins")
					.source(AssetSource.artifact(plugin.artifact()))
					.target(Path.of("plugins", plugin.fileName() + ".jar"))
					.build());
		for (Module module : socialismus.modules())
			workspace.asset(moduleJar(data, module.artifact(), module.moduleName()));
		for (ModuleFixture fixture : socialismus.moduleFixtures())
			workspace.asset(moduleJar(data, fixture.artifact(), fixture.moduleName()));

		if (!socialismus.data().isBlank())
			seeds(socialismus.data()).forEach(file -> workspace.asset(WorkspaceAsset.builder()
					.group("socialismus-data")
					.source(AssetSource.path(file.source()))
					.target(Path.of(data).resolve(file.path()))
					.mode(AssetInstallMode.SEED_ONCE)
					.build()));

		files.files().forEach((path, content) -> workspace.asset(WorkspaceAsset.builder()
				.group("socialismus-configuration")
				.source(AssetSource.text(content))
				.target(Path.of(data, path))
				.build()));

		return workspace.build();
	}

	private static WorkspaceAsset moduleJar(String data, String artifact, String moduleName) {
		return WorkspaceAsset.builder()
				.group("socialismus-modules")
				.source(AssetSource.artifact(artifact))
				.target(Path.of(data, "modules", moduleName + ".jar"))
				.build();
	}

	private static List<Seed> seeds(String resource) {
		URL url = SocialismusScenarios.class.getClassLoader().getResource(resource);
		if (url == null) throw new IllegalArgumentException("The test resources have no directory " + resource);

		try {
			Path root = Path.of(url.toURI());
			try (Stream<Path> paths = Files.walk(root)) {
				return paths.filter(Files::isRegularFile)
						.map(path -> new Seed(path, root.relativize(path)))
						.toList();
			}
		} catch (IOException failure) {
			throw new UncheckedIOException("Could not list " + resource, failure);
		} catch (URISyntaxException failure) {
			throw new IllegalArgumentException("Could not locate " + resource, failure);
		}
	}

	private static Consumer<ServerFiles> instantiate(Class<? extends Consumer<ServerFiles>> change) {
		try {
			var constructor = change.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (ReflectiveOperationException failure) {
			throw new IllegalStateException(change.getName() + " needs a constructor without parameters", failure);
		}
	}

	/**
	 * Names the network after everything that sets it apart, so different networks never share a workspace.
	 */
	private static String name(Socialismus socialismus) {
		return Stream.of(
						"socialismus",
						socialismus.network().name(),
						socialismus.bundle() ? "bundle" : "",
						socialismus.data().replace('/', '-'),
						Arrays.stream(socialismus.plugins()).map(Plugin::name).collect(Collectors.joining("+")),
						Arrays.stream(socialismus.modules()).map(Module::name).collect(Collectors.joining("+")),
						Arrays.stream(socialismus.moduleFixtures()).map(ModuleFixture::name).collect(Collectors.joining("+")),
						Arrays.stream(socialismus.containers()).map(Container::name).collect(Collectors.joining("+")),
						Arrays.stream(socialismus.configure()).map(SocialismusScenarios::changeName).collect(Collectors.joining("+")),
						Arrays.stream(socialismus.proxyConfigure()).map(SocialismusScenarios::changeName).collect(Collectors.joining("+")))
				.filter(part -> !part.isBlank())
				.collect(Collectors.joining("-"))
				.toLowerCase(Locale.ROOT);
	}

	/**
	 * Names a change by its class and the class it is declared in, since journeys of different classes give their
	 * changes the same simple name.
	 */
	private static String changeName(Class<?> change) {
		Class<?> owner = change.getEnclosingClass();
		return owner == null ? change.getSimpleName() : owner.getSimpleName() + "." + change.getSimpleName();
	}

	private record Seed(Path source, Path path) {
	}
}
