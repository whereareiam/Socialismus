package me.whereareiam.socialismus.module;

import me.whereareiam.configura.Config;
import me.whereareiam.configura.type.Format;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.module.model.InternalModule;
import me.whereareiam.socialismus.module.type.ModuleState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
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
		String moduleJson = """
				{
				  "name": "%s",
				  "version": "1.0.0",
				  "authors": ["whereareiam"],
				  "supportedPlatforms": ["PAPER"],
				  "supportedVersions": ["V_1_21"],
				  "dependencies": [],
				  "main": "me.whereareiam.socialismus.module.%s"
				}
				""".formatted(name, name);

		try (OutputStream file = Files.newOutputStream(modulesPath.resolve(name + ".jar"));
		     JarOutputStream jar = new JarOutputStream(file)) {
			jar.putNextEntry(new JarEntry("module.json"));
			jar.write(moduleJson.getBytes(StandardCharsets.UTF_8));
			jar.closeEntry();
		}
	}
}
