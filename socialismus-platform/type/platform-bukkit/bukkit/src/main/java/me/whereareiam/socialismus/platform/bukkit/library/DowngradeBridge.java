package me.whereareiam.socialismus.platform.bukkit.library;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import xyz.wagyourtail.jvmdg.ClassDowngrader;
import xyz.wagyourtail.jvmdg.cli.Flags;
import xyz.wagyourtail.jvmdg.compile.ApiShader;
import xyz.wagyourtail.jvmdg.compile.ZipDowngrader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * Calls JvmDowngrader. {@link LibraryDowngrade} loads this class in a class loader of its own above the JDK, because
 * JvmDowngrader reads the JDK's class files through its class loader and a Bukkit plugin's class loader hides them;
 * so it takes and returns JDK types only.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DowngradeBridge {
	/**
	 * Downgrades a jar and copies the stand-ins of the newer Java APIs it calls into it under a package of its own.
	 *
	 * @param classVersion the class file version to downgrade to
	 * @param javaApi      JvmDowngrader's Java API stand-ins
	 * @param input        the jar to downgrade
	 * @param output       the file to write the downgraded jar to
	 * @param shadePrefix  the path the stand-ins are copied under, ending in a slash
	 */
	public static void downgrade(int classVersion, File javaApi, Path input, Path output, String shadePrefix) throws IOException {
		Flags flags = new Flags();
		flags.classVersion = classVersion;
		flags.api = List.of(javaApi);
		flags.quiet = true;

		Path downgraded = Files.createTempFile(output.getParent(), "downgrade", ".jar");
		try {
			try (ClassDowngrader downgrader = ClassDowngrader.downgradeTo(flags)) {
				ZipDowngrader.downgradeZip(downgrader, input, Set.of(), downgraded);
			}
			ApiShader.shadeApis(flags, shadePrefix, downgraded.toFile(), output.toFile(), Set.of(javaApi));
		} finally {
			Files.deleteIfExists(downgraded);
		}
	}
}
