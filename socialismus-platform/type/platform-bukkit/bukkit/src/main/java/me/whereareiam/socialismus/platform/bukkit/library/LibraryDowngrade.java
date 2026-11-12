package me.whereareiam.socialismus.platform.bukkit.library;

import me.whereareiam.attache.LibraryManager;
import me.whereareiam.attache.LibraryTransformer;
import me.whereareiam.attache.model.LibraryRequest;
import me.whereareiam.attache.model.RelocationRule;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HexFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Rewrites a library compiled for a newer Java than the server runs, such as Adventure for Java 21 on a Minecraft
 * 1.16.5 server on Java 11, with JvmDowngrader, so the BUKKIT jar runs on the servers its descriptor names.
 * <p>
 * A library the running Java can load passes unchanged. A downgraded library carries the stand-ins of the newer Java
 * APIs it calls under a package of its own, so libraries never share them.
 */
public final class LibraryDowngrade implements LibraryTransformer {
	private static final String GROUP = "xyz.wagyourtail.jvmdowngrader";
	private static final String VERSION = "2.0.1";
	// The SHA-256 of jvmdowngrader-java-api 2.0.1 on Maven Central.
	private static final String JAVA_API_SHA256 = "e52a32873f878e64898bc04c7032cd08ed537b9e286f6b466c80c0724a209d2c";
	private static final int JAVA_CLASS_VERSION_OFFSET = 44;
	// Raised whenever this transformer writes different jars, so servers downgrade their libraries again.
	private static final int REVISION = 2;

	private final LibraryManager libraries;
	private final int java = Runtime.version().feature();
	private Path javaApi;
	private Method downgrade;

	/**
	 * @param libraries the manager that downloads JvmDowngrader's Java API stand-ins once a library needs them
	 */
	public LibraryDowngrade(@NotNull LibraryManager libraries) {
		this.libraries = libraries;
	}

	@Override
	public @NotNull String getId() {
		return "downgrade";
	}

	@Override
	public @NotNull String getVersion() {
		return VERSION + "-" + REVISION + "-java" + java;
	}

	@Override
	public boolean appliesTo(@NotNull LibraryRequest library) {
		return !library.getGroupId().equals(GROUP);
	}

	@Override
	public synchronized @NotNull Path transform(@NotNull LibraryRequest library, @NotNull Path input, @NotNull Path output) throws IOException {
		int classVersion = java + JAVA_CLASS_VERSION_OFFSET;
		if (newestClassVersion(input) <= classVersion) return input;

		String prefix = "me/whereareiam/socialismus/library/jvmdg/" + library.getArtifactId().replaceAll("[^A-Za-z0-9]", "_") + "/";
		try {
			downgrade().invoke(null, classVersion, javaApi().toFile(), input, output, prefix);
		} catch (InvocationTargetException failure) {
			if (failure.getCause() instanceof IOException io) throw io;
			throw new IOException("Could not downgrade " + library.getArtifactId(), failure.getCause());
		} catch (ReflectiveOperationException failure) {
			throw new IOException("Could not reach JvmDowngrader", failure);
		}

		return output;
	}

	/**
	 * Returns {@link DowngradeBridge#downgrade}, loaded from this jar by a class loader above the JDK alone.
	 */
	private Method downgrade() throws ReflectiveOperationException {
		if (downgrade != null) return downgrade;

		URL jar = LibraryDowngrade.class.getProtectionDomain().getCodeSource().getLocation();
		// Never closed: it serves every library this server loads, and closing it would unload JvmDowngrader.
		ClassLoader loader = new URLClassLoader(new URL[]{jar}, ClassLoader.getPlatformClassLoader());
		downgrade = Class.forName(DowngradeBridge.class.getName(), true, loader)
				.getMethod("downgrade", int.class, File.class, Path.class, Path.class, String.class);

		return downgrade;
	}

	private Path javaApi() throws IOException {
		if (javaApi != null) return javaApi;

		LibraryRequest request = LibraryRequest.builder()
				.groupId(GROUP)
				.artifactId("jvmdowngrader-java-api")
				.version(VERSION)
				.checksum(HexFormat.of().parseHex(JAVA_API_SHA256))
				// Its downgrading steps call ASM, which this jar relocates; "{}" keeps the build from relocating the pattern.
				.relocation(RelocationRule.builder()
						.pattern("org{}objectweb{}asm")
						.relocatedPattern("me.whereareiam.socialismus.library.asm")
						.build())
				.build();
		try {
			javaApi = libraries.downloadLibrary(request);
		} catch (IOException failure) {
			throw failure;
		} catch (Exception failure) {
			throw new IOException("Could not download JvmDowngrader's Java API stand-ins", failure);
		}

		return javaApi;
	}

	/**
	 * Returns the newest class file version in a jar, leaving out the classes of newer Javas in a multi-release jar.
	 */
	private static int newestClassVersion(Path jar) throws IOException {
		int newest = 0;
		try (ZipFile zip = new ZipFile(jar.toFile())) {
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				String name = entry.getName();
				if (!name.endsWith(".class") || name.startsWith("META-INF/") || name.endsWith("module-info.class"))
					continue;

				try (InputStream in = zip.getInputStream(entry)) {
					byte[] header = in.readNBytes(8);
					if (header.length == 8) newest = Math.max(newest, ((header[6] & 0xFF) << 8) | (header[7] & 0xFF));
				}
			}
		}

		return newest;
	}
}
