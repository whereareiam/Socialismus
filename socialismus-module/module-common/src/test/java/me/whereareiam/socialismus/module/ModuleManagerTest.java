package me.whereareiam.socialismus.module;

import me.whereareiam.configura.Config;
import me.whereareiam.configura.type.Format;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.module.model.InternalModule;
import me.whereareiam.socialismus.module.type.ModuleState;
import me.whereareiam.socialismus.type.PlatformType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InOrder;
import org.mockito.stubbing.Stubber;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

@DisplayName("Module manager")
class ModuleManagerTest {
	private final List<String> warnings = new ArrayList<>();
	private final ModuleLifecycleController lifecycleController = mock(ModuleLifecycleController.class);

	@TempDir
	private Path modulesPath;

	@BeforeEach
	void recordWarnings() {
		LoggingHelper loggingHelper = mock(LoggingHelper.class);
		doAnswer(call -> warnings.add(call.getArgument(0, String.class)))
				.when(loggingHelper).warn(anyString(), any(Object[].class));
		Logger.init(loggingHelper);

		moveOn(ModuleState.UNKNOWN, ModuleState.ENABLED).when(lifecycleController).loadModule(any());
		moveOn(ModuleState.ENABLED, ModuleState.UNLOADED).when(lifecycleController).unloadModule(any());
	}

	@DisplayName("Forgets the modules that unloaded without warning about them")
	@Test
	void forgetsUnloadedModules() {
		ModuleManager manager = manager(module("Chirper", ModuleState.ENABLED), module("Essentials", ModuleState.ENABLED));

		manager.unloadModules();

		assertEquals(List.of(), manager.getModules());
		assertEquals(List.of(), warnings);
	}

	@DisplayName("Keeps and warns about a module that is still loaded")
	@Test
	void keepsModulesThatStayedLoaded() {
		InternalModule stuck = module("Chirper", ModuleState.DISABLED);
		ModuleManager manager = manager(stuck, module("Essentials", ModuleState.ENABLED));

		manager.unloadModules();

		assertEquals(List.of(stuck), manager.getModules());
		assertEquals(List.of("Module was not unloaded: Chirper"), warnings);
	}

	@DisplayName("Forgets a module that was refused without warning about it")
	@Test
	void forgetsRefusedModules() {
		ModuleManager manager = manager(module("Chirper", ModuleState.ERROR), module("Bubbler", ModuleState.WAITING_FOR_DEPENDENCY));

		manager.unloadModules();

		assertEquals(List.of(), manager.getModules());
		assertEquals(List.of(), warnings);
	}

	@DisplayName("Loads the modules again on reload")
	@Test
	void reloadsModules() throws IOException {
		Config.setConfigured(Config.builder().format(Format.JSON).build());
		writeModuleJar("Chirper");
		ModuleManager manager = new ModuleManager(modulesPath, lifecycleController);
		manager.loadModules();

		manager.reloadModules();

		assertEquals(1, manager.getModules().size());
		assertEquals(ModuleState.ENABLED, manager.getModules().getFirst().getState());
		assertEquals(List.of(), warnings);
	}

	@DisplayName("Loads a module whose optional dependency is not installed")
	@Test
	void loadsAModuleWithoutItsOptionalDependency() throws IOException {
		Config.setConfigured(Config.builder().format(Format.JSON).build());
		writeModuleJar("Dialogue", """
				[{"type": "MODULE", "name": "SocialismusDatabase", "optional": true}]""");
		ModuleManager manager = new ModuleManager(modulesPath, lifecycleController);

		manager.loadModules();

		assertEquals(List.of("Dialogue"), manager.getModules().stream().map(InternalModule::getName).toList());
		assertTrue(manager.getModules().getFirst().getDependencies().getFirst().isOptional());
	}

	@DisplayName("Loads a module after the module it depends on")
	@Test
	void loadsADependencyFirst() throws IOException {
		Config.setConfigured(Config.builder().format(Format.JSON).build());
		writeModuleJar("Dialogue", """
				[{"type": "MODULE", "name": "SocialismusDatabase", "optional": true}]""");
		writeModuleJar("SocialismusDatabase", "[]");
		ModuleManager manager = new ModuleManager(modulesPath, lifecycleController);

		manager.loadModules();

		assertEquals(List.of("SocialismusDatabase", "Dialogue"),
				manager.getModules().stream().map(InternalModule::getName).toList());
	}

	@DisplayName("Keeps everything a module.json says")
	@Test
	void keepsEverythingTheModuleJsonSays() throws IOException {
		Config.setConfigured(Config.builder().format(Format.JSON).build());
		writeJar("SocialismusDatabase", """
				{
				  "name": "SocialismusDatabase",
				  "version": "1.0.0",
				  "authors": ["whereareiam"],
				  "socialismus": "dev",
				  "platforms": [{"type": "PAPER", "version": "26\\\\.1"}],
				  "requirements": [{"resource": "socialismus:sync", "optional": true}],
				  "exports": ["me.whereareiam.socialismus.module.database.api", "org.jdbi"],
				  "main": "me.whereareiam.socialismus.module.SocialismusDatabase"
				}
				""");
		ModuleManager manager = new ModuleManager(modulesPath, lifecycleController);

		manager.loadModules();

		InternalModule module = manager.getModules().getFirst();
		assertEquals("dev", module.getSocialismus());
		assertEquals(PlatformType.PAPER, module.getPlatforms().getFirst().getType());
		assertEquals("26\\.1", module.getPlatforms().getFirst().getVersion());
		assertEquals("socialismus:sync", module.getRequirements().getFirst().getResource());
		assertEquals(List.of("me.whereareiam.socialismus.module.database.api", "org.jdbi"), module.getExports());
	}

	@DisplayName("Refuses a module.json that does not name the Socialismus versions it is built for")
	@Test
	void refusesAModuleWithoutItsSocialismusVersions() throws IOException {
		Config.setConfigured(Config.builder().format(Format.JSON).build());
		writeJar("Chirper", """
				{"name": "Chirper", "version": "1.0.0", "authors": [], "platforms": [{"type": "PAPER"}], "main": "Chirper"}
				""");
		ModuleManager manager = new ModuleManager(modulesPath, lifecycleController);

		manager.loadModules();

		assertEquals(List.of(), manager.getModules());
	}

	@DisplayName("Refuses a module.json that exports a prefix instead of a package name")
	@Test
	void refusesAnExportThatIsNoPackageName() throws IOException {
		Config.setConfigured(Config.builder().format(Format.JSON).build());
		writeJar("SocialismusDatabase", """
				{"name": "SocialismusDatabase", "version": "1.0.0", "authors": [], "socialismus": "dev",
				 "platforms": [{"type": "PAPER"}], "exports": ["org.jdbi."], "main": "SocialismusDatabase"}
				""");
		ModuleManager manager = new ModuleManager(modulesPath, lifecycleController);

		manager.loadModules();

		assertEquals(List.of(), manager.getModules());
	}

	@DisplayName("Stops a module before the module it depends on")
	@Test
	void stopsDependentsFirst() throws IOException {
		Config.setConfigured(Config.builder().format(Format.JSON).build());
		writeModuleJar("Dialogue", """
				[{"type": "MODULE", "name": "SocialismusDatabase"}]""");
		writeModuleJar("SocialismusDatabase", "[]");
		ModuleManager manager = new ModuleManager(modulesPath, lifecycleController);
		manager.loadModules();
		InternalModule database = manager.getModule("SocialismusDatabase").orElseThrow();
		InternalModule dialogue = manager.getModule("Dialogue").orElseThrow();

		manager.unloadModules();

		InOrder order = inOrder(lifecycleController);
		order.verify(lifecycleController).disableModule(dialogue);
		order.verify(lifecycleController).disableModule(database);
		order.verify(lifecycleController).unloadModule(dialogue);
		order.verify(lifecycleController).unloadModule(database);
	}

	private Stubber moveOn(ModuleState from, ModuleState to) {
		return doAnswer(call -> {
			InternalModule module = call.getArgument(0, InternalModule.class);
			if (module.getState() == from) module.setState(to);
			return null;
		});
	}

	private ModuleManager manager(InternalModule... modules) {
		ModuleManager manager = new ModuleManager(modulesPath, lifecycleController);
		manager.setModules(new ArrayList<>(List.of(modules)));

		return manager;
	}

	private InternalModule module(String name, ModuleState state) {
		return InternalModule.builder()
				.name(name)
				.version("1.0.0")
				.dependencies(List.of())
				.state(state)
				.build();
	}

	private void writeModuleJar(String name) throws IOException {
		writeModuleJar(name, "[]");
	}

	private void writeModuleJar(String name, String dependencies) throws IOException {
		String moduleJson = """
				{
				  "name": "%s",
				  "version": "1.0.0",
				  "authors": ["whereareiam"],
				  "socialismus": "dev",
				  "platforms": [{"type": "PAPER"}],
				  "dependencies": %s,
				  "main": "me.whereareiam.socialismus.module.%s"
				}
				""".formatted(name, dependencies, name);
		writeJar(name, moduleJson);
	}

	private void writeJar(String name, String moduleJson) throws IOException {

		try (OutputStream file = Files.newOutputStream(modulesPath.resolve(name + ".jar"));
		     JarOutputStream jar = new JarOutputStream(file)) {
			jar.putNextEntry(new JarEntry("module.json"));
			jar.write(moduleJson.getBytes(StandardCharsets.UTF_8));
			jar.closeEntry();
		}
	}
}
