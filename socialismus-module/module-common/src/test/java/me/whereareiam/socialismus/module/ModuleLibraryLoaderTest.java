package me.whereareiam.socialismus.module;

import me.whereareiam.attache.LoggingHelper;
import me.whereareiam.attache.platform.standalone.StandaloneLibraryManager;
import me.whereareiam.attache.type.Level;
import me.whereareiam.socialismus.module.model.InternalModule;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class ModuleLibraryLoaderTest {
	@TempDir
	Path temporaryDirectory;

	@Test
	void loadsTheLibrariesAModuleDeclaresIntoItsOwnClassLoader() throws Exception {
		Path library = libraryJar("greeting", "dev.example.greeting.Greeting");
		Path module = jarWithDescriptor("module", descriptor("module", library("greeting", library, null)));
		Path plugin = directoryWithDescriptor("plugin", descriptor("plugin", ""));

		try (URLClassLoader pluginLoader = new URLClassLoader(new URL[]{plugin.toUri().toURL()}, null);
		     StandaloneLibraryManager pluginLibraries = pluginLibraries(pluginLoader);
		     URLClassLoader moduleLoader = new URLClassLoader(new URL[]{module.toUri().toURL()}, pluginLoader);
		     ModuleLibraryLoader.LoadedModuleLibraries ignored = new ModuleLibraryLoader(pluginLibraries)
				     .loadOwnLibraries(module("Example", module), moduleLoader)) {
			assertNotNull(moduleLoader.loadClass("dev.example.greeting.Greeting"));
			assertThrows(ClassNotFoundException.class, () -> pluginLoader.loadClass("dev.example.greeting.Greeting"));
		}
	}

	@Test
	void leavesTheLibrariesOfSocialismusToItsOwnClassLoader() throws Exception {
		Path pluginLibrary = libraryJar("plugin-library", "dev.example.plugin.PluginLibrary");
		Path module = jarWithDescriptor("module", descriptor("module", ""));
		Path plugin = directoryWithDescriptor("plugin", descriptor("plugin", library("plugin-library", pluginLibrary, null)));

		try (URLClassLoader pluginLoader = new URLClassLoader(new URL[]{plugin.toUri().toURL()}, null);
		     StandaloneLibraryManager pluginLibraries = pluginLibraries(pluginLoader);
		     URLClassLoader moduleLoader = new URLClassLoader(new URL[]{module.toUri().toURL()}, pluginLoader);
		     ModuleLibraryLoader.LoadedModuleLibraries ignored = new ModuleLibraryLoader(pluginLibraries)
				     .loadOwnLibraries(module("Example", module), moduleLoader)) {
			assertThrows(ClassNotFoundException.class, () -> moduleLoader.loadClass("dev.example.plugin.PluginLibrary"));
		}
	}

	@Test
	void loadsAGroupOfTheModuleWhenItAsks() throws Exception {
		Path driver = libraryJar("driver", "dev.example.driver.Driver");
		Path module = jarWithDescriptor("module", descriptor("module", library("driver", driver, "sqlite")));
		Path plugin = directoryWithDescriptor("plugin", descriptor("plugin", ""));

		try (URLClassLoader pluginLoader = new URLClassLoader(new URL[]{plugin.toUri().toURL()}, null);
		     StandaloneLibraryManager pluginLibraries = pluginLibraries(pluginLoader);
		     URLClassLoader moduleLoader = new URLClassLoader(new URL[]{module.toUri().toURL()}, pluginLoader);
		     ModuleLibraryLoader.LoadedModuleLibraries libraries = new ModuleLibraryLoader(pluginLibraries)
				     .loadOwnLibraries(module("Example", module), moduleLoader)) {
			assertThrows(ClassNotFoundException.class, () -> moduleLoader.loadClass("dev.example.driver.Driver"));

			libraries.loadGroups("sqlite");

			assertNotNull(moduleLoader.loadClass("dev.example.driver.Driver"));
			assertThrows(IllegalArgumentException.class, () -> libraries.loadGroups("postgres"));
		}
	}

	private StandaloneLibraryManager pluginLibraries(ClassLoader pluginLoader) {
		return new StandaloneLibraryManager(new SilentLogging(), temporaryDirectory.resolve("data"), ".libraries", pluginLoader);
	}

	private static InternalModule module(String name, Path jar) {
		return InternalModule.builder()
				.name(name)
				.path(jar)
				.dependencies(List.of())
				.build();
	}

	private static String descriptor(String project, String libraries) {
		return """
				<?xml version="1.0" encoding="UTF-8"?>
				<attache-descriptor project-path=":%s" project-name="%s" add-maven-central="false">
				  <libraries>
				%s
				  </libraries>
				</attache-descriptor>
				""".formatted(project, project, libraries);
	}

	private static String library(String artifact, Path jar, String group) {
		return """
				    <library group-id="dev.example" artifact-id="%s" version="1.0.0"%s>
				      <urls><url>%s</url></urls>
				    </library>
				""".formatted(artifact, group == null ? "" : " group=\"" + group + "\"", jar.toUri());
	}

	private Path directoryWithDescriptor(String name, String descriptor) throws IOException {
		Path directory = temporaryDirectory.resolve(name);
		Path file = directory.resolve("META-INF/attache/" + name + "/attache.xml");
		Files.createDirectories(file.getParent());
		Files.writeString(file, descriptor, StandardCharsets.UTF_8);

		return directory;
	}

	private Path jarWithDescriptor(String name, String descriptor) throws IOException {
		Path jar = temporaryDirectory.resolve(name + ".jar");
		try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(jar))) {
			output.putNextEntry(new JarEntry("META-INF/attache/" + name + "/attache.xml"));
			output.write(descriptor.getBytes(StandardCharsets.UTF_8));
			output.closeEntry();
		}

		return jar;
	}

	private Path libraryJar(String name, String className) throws IOException {
		int split = className.lastIndexOf('.');
		Path sourceFile = temporaryDirectory.resolve(name + "-source").resolve(className.replace('.', '/') + ".java");
		Path classes = temporaryDirectory.resolve(name + "-classes");
		Files.createDirectories(sourceFile.getParent());
		Files.writeString(sourceFile, "package " + className.substring(0, split) + "; public final class "
				+ className.substring(split + 1) + " {}");

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		assertNotNull(compiler, "Tests require a JDK with the Java compiler available");
		try (StandardJavaFileManager files = compiler.getStandardFileManager(null, null, null)) {
			assertTrue(compiler.getTask(null, files, null, List.of("--release", "21", "-d", classes.toString()), null,
					files.getJavaFileObjects(sourceFile)).call());
		}

		Path jar = temporaryDirectory.resolve(name + ".jar");
		try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(jar)); var paths = Files.walk(classes)) {
			for (Path file : paths.filter(Files::isRegularFile).toList()) {
				output.putNextEntry(new JarEntry(classes.relativize(file).toString().replace(File.separatorChar, '/')));
				Files.copy(file, output);
				output.closeEntry();
			}
		}

		return jar;
	}

	private static final class SilentLogging implements LoggingHelper {
		@Override
		public void log(@NotNull Level level, @NotNull String message) {
		}

		@Override
		public void log(@NotNull Level level, @NotNull String message, @NotNull Throwable throwable) {
		}
	}
}
