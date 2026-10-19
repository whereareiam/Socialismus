package me.whereareiam.socialismus.module;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ModuleClassLoaderTest {
	@TempDir
	Path temporaryDirectory;

	@Test
	void reachesEveryClassOfAPluginItDependsOn() throws Exception {
		Path pluginJar = compileJar("plugin", Map.of("dev.example.clans.api.ClanApi", """
				package dev.example.clans.api;

				public final class ClanApi {
					public static String chatName() {
						return "dummy-clan-chat";
					}
				}
				"""), List.of());
		Path moduleJar = compileJar("module", Map.of("dev.example.module.DummyClanModule", """
				package dev.example.module;

				import dev.example.clans.api.ClanApi;

				public final class DummyClanModule {
					public String chatName() {
						return ClanApi.chatName();
					}
				}
				"""), List.of(pluginJar));

		try (URLClassLoader plugin = loaderOf(pluginJar);
		     ModuleClassLoader moduleClassLoader = moduleLoader(moduleJar, Map.of(), List.of(plugin))) {
			Class<?> moduleClass = Class.forName("dev.example.module.DummyClanModule", true, moduleClassLoader);
			Object module = moduleClass.getConstructor().newInstance();

			assertEquals("dummy-clan-chat", moduleClass.getMethod("chatName").invoke(module));
			assertSame(plugin, moduleClassLoader.loadClass("dev.example.clans.api.ClanApi").getClassLoader());
		}
	}

	@Test
	void prefersItsOwnClassToAPluginsCopy() throws Exception {
		Map<String, String> shared = Map.of("dev.example.shared.Shared", """
				package dev.example.shared;

				public final class Shared {
				}
				""");
		Path pluginJar = compileJar("plugin", shared, List.of());
		Path moduleJar = compileJar("module", shared, List.of());

		try (URLClassLoader plugin = loaderOf(pluginJar);
		     ModuleClassLoader moduleClassLoader = moduleLoader(moduleJar, Map.of(), List.of(plugin))) {
			assertSame(moduleClassLoader, moduleClassLoader.loadClass("dev.example.shared.Shared").getClassLoader());
		}
	}

	@Test
	void reachesOnlyTheExportedPackagesOfAModule() throws Exception {
		Path libraryJar = compileJar("library", Map.of(
				"dev.example.library.api.Greeter", "package dev.example.library.api; public final class Greeter {}",
				"dev.example.library.api.model.Greeting", "package dev.example.library.api.model; public final class Greeting {}",
				"dev.example.library.internal.Secret", "package dev.example.library.internal; public final class Secret {}",
				"dev.example.library.apix.Lookalike", "package dev.example.library.apix; public final class Lookalike {}"
		), List.of());
		Path moduleJar = compileJar("module", Map.of(
				"dev.example.module.Consumer", "package dev.example.module; public final class Consumer {}"
		), List.of());

		try (URLClassLoader library = loaderOf(libraryJar);
		     ModuleClassLoader moduleClassLoader = moduleLoader(moduleJar, Map.of("dev.example.library.api", library), List.of())) {
			assertSame(library, moduleClassLoader.loadClass("dev.example.library.api.Greeter").getClassLoader());
			assertSame(library, moduleClassLoader.loadClass("dev.example.library.api.model.Greeting").getClassLoader());
			assertThrows(ClassNotFoundException.class, () -> moduleClassLoader.loadClass("dev.example.library.internal.Secret"));
			assertThrows(ClassNotFoundException.class, () -> moduleClassLoader.loadClass("dev.example.library.apix.Lookalike"));
		}
	}

	private URLClassLoader loaderOf(Path jar) throws Exception {
		return new URLClassLoader(new URL[]{jar.toUri().toURL()}, getClass().getClassLoader());
	}

	private ModuleClassLoader moduleLoader(
			Path moduleJar,
			Map<String, ClassLoader> exports,
			List<ClassLoader> platformDependencies
	) throws Exception {
		return new ModuleClassLoader(moduleJar.toUri().toURL(), getClass().getClassLoader(), exports, platformDependencies);
	}

	private Path compileJar(String name, Map<String, String> sources, List<Path> classpath) throws IOException {
		Path sourceDirectory = temporaryDirectory.resolve(name + "-source");
		Path classesDirectory = temporaryDirectory.resolve(name + "-classes");
		List<File> sourceFiles = new ArrayList<>();
		for (Map.Entry<String, String> source : sources.entrySet()) {
			Path sourceFile = sourceDirectory.resolve(source.getKey().replace('.', '/') + ".java");
			Files.createDirectories(sourceFile.getParent());
			Files.writeString(sourceFile, source.getValue());
			sourceFiles.add(sourceFile.toFile());
		}

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		assertNotNull(compiler, "Tests require a JDK with the Java compiler available");

		try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
			List<String> options = List.of(
					"--release", "21",
					"-classpath", classpath.stream().map(Path::toString).collect(Collectors.joining(File.pathSeparator)),
					"-d", classesDirectory.toString()
			);
			Boolean compiled = compiler.getTask(
					null,
					fileManager,
					null,
					options,
					null,
					fileManager.getJavaFileObjectsFromFiles(sourceFiles)
			).call();
			assertTrue(compiled);
		}

		Path jar = temporaryDirectory.resolve(name + ".jar");
		try (JarOutputStream output = new JarOutputStream(Files.newOutputStream(jar)); var files = Files.walk(classesDirectory)) {
			for (Path file : files.filter(Files::isRegularFile).toList()) {
				String entryName = classesDirectory.relativize(file).toString().replace(File.separatorChar, '/');
				output.putNextEntry(new JarEntry(entryName));
				Files.copy(file, output);
				output.closeEntry();
			}
		}

		return jar;
	}
}
