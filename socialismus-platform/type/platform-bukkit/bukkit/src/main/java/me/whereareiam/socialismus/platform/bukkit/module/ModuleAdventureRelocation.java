package me.whereareiam.socialismus.platform.bukkit.module;

import me.whereareiam.chassis.ExtensionTransformer;
import me.whereareiam.chassis.model.ExtensionDescriptor;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Points a module's references to Adventure at the relocated Adventure the BUKKIT jar carries, since modules compile
 * against {@code net.kyori.adventure} and the BUKKIT jar keeps its own Adventure apart from the one Paper ships.
 */
public final class ModuleAdventureRelocation implements ExtensionTransformer {
	// Written with "{}" so the build's relocation of the BUKKIT jar leaves the original names in place.
	private static final Map<String, String> PACKAGES = Map.of(
			"net{}kyori{}adventure{}".replace("{}", "/"), "me/whereareiam/socialismus/library/adventure/",
			"net{}kyori{}option{}".replace("{}", "/"), "me/whereareiam/socialismus/library/option/"
	);

	private final Remapper remapper = new Remapper() {
		@Override
		public String map(String internalName) {
			return relocate(internalName);
		}
	};

	@Override
	public @NotNull String getId() {
		return "adventure-relocation";
	}

	@Override
	public @NotNull String getVersion() {
		return "1";
	}

	@Override
	public @NotNull Path transform(@NotNull ExtensionDescriptor extension, @NotNull Path input, @NotNull Path output) throws IOException {
		try (ZipInputStream in = new ZipInputStream(Files.newInputStream(input));
			 ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(output))) {
			for (ZipEntry entry = in.getNextEntry(); entry != null; entry = in.getNextEntry()) {
				byte[] bytes = in.readAllBytes();
				String name = entry.getName();
				if (name.endsWith(".class")) {
					bytes = relocateClass(bytes);
					name = relocate(name);
				} else if (name.startsWith("META-INF/services/")) {
					bytes = relocateDotted(new String(bytes, StandardCharsets.UTF_8)).getBytes(StandardCharsets.UTF_8);
					name = "META-INF/services/" + relocateDotted(name.substring("META-INF/services/".length()));
				}

				out.putNextEntry(new ZipEntry(name));
				out.write(bytes);
				out.closeEntry();
			}
		}

		return output;
	}

	private byte[] relocateClass(byte[] bytes) {
		ClassReader reader = new ClassReader(bytes);
		ClassWriter writer = new ClassWriter(0);
		reader.accept(new ClassRemapper(writer, remapper), 0);
		return writer.toByteArray();
	}

	private static String relocate(String path) {
		for (Map.Entry<String, String> relocation : PACKAGES.entrySet())
			if (path.startsWith(relocation.getKey()))
				return relocation.getValue() + path.substring(relocation.getKey().length());

		return path;
	}

	private static String relocateDotted(String text) {
		for (Map.Entry<String, String> relocation : PACKAGES.entrySet())
			text = text.replace(relocation.getKey().replace('/', '.'), relocation.getValue().replace('/', '.'));

		return text;
	}
}
