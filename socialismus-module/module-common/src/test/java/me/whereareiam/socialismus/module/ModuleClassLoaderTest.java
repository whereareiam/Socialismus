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
	void loadsDeclaredExternalPluginApiFromPluginClassLoader() throws Exception {
		Path pluginJar = compileJar(
				"plugin",
				"dev.example.clans.api.ClanApi",
				"""
						package dev.example.clans.api;

						public final class ClanApi {
							private ClanApi() {
							}

							public static String chatName() {
								return \"dummy-clan-chat\";
							}
						}
						""",
				List.of()
		);
		Path moduleJar = compileJar(
				"module",
				"dev.example.module.DummyClanModule",
				"""
						package dev.example.module;

						import dev.example.clans.api.ClanApi;
						import me.whereareiam.socialismus.module.SocialisticModule;

						public final class DummyClanModule extends SocialisticModule {
							public String chatName() {
								return ClanApi.chatName();
							}

							@Override
							public void onLoad() {
							}

							@Override
							public void onEnable() {
							}

							@Override
							public void onDisable() {
							}

							@Override
							public void onUnload() {
							}
						}
						""",
				List.of(pluginJar, moduleApiLocation())
		);

		try (URLClassLoader pluginClassLoader = new URLClassLoader(
				new URL[]{pluginJar.toUri().toURL()},
				getClass().getClassLoader()
		); ModuleClassLoader moduleClassLoader = new ModuleClassLoader(
				moduleJar.toUri().toURL(),
				getClass().getClassLoader(),
				Map.of("dev.example.clans.api.", pluginClassLoader)
		)) {
			Class<?> moduleClass = Class.forName("dev.example.module.DummyClanModule", true, moduleClassLoader);
			Object module = moduleClass.getConstructor().newInstance();
			Class<?> apiClass = moduleClassLoader.loadClass("dev.example.clans.api.ClanApi");

			assertEquals("dummy-clan-chat", moduleClass.getMethod("chatName").invoke(module));
			assertSame(pluginClassLoader, apiClass.getClassLoader());
		}
	}

	private Path compileJar(String name, String className, String source, List<Path> classpath) throws IOException {
		Path sourceDirectory = temporaryDirectory.resolve(name + "-source");
		Path classesDirectory = temporaryDirectory.resolve(name + "-classes");
		Path sourceFile = sourceDirectory.resolve(className.replace('.', '/') + ".java");
		Files.createDirectories(sourceFile.getParent());
		Files.writeString(sourceFile, source);

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
					fileManager.getJavaFileObjects(sourceFile)
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

	private Path moduleApiLocation() throws Exception {
		return Path.of(SocialisticModule.class.getProtectionDomain().getCodeSource().getLocation().toURI());
	}
}
