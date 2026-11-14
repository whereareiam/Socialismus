package me.whereareiam.socialismus.module.mentionator.common.config.provider;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.logging.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A folder of config files that each hold a list, such as {@code tags/}: every file is read as it is written, and an
 * empty folder first gets the default files.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ConfigFolder {
	/**
	 * Reads every file of a folder.
	 *
	 * @param configura reads and writes the files
	 * @param folder    the folder
	 * @param type      the model of a file
	 * @param defaults  the default files by name, written when the folder holds none
	 * @param <C>       the model of a file
	 * @return the files, in the order of their names
	 */
	static <C> List<C> read(Configura configura, Path folder, Class<C> type, Supplier<Map<String, C>> defaults) {
		try {
			Files.createDirectories(folder);
			if (names(folder).isEmpty()) defaults.get().forEach((name, file) -> configura.write(folder.resolve(name), file));

			List<C> files = new ArrayList<>();
			for (String name : names(folder)) {
				try {
					files.add(configura.read(folder.resolve(name), type));
				} catch (RuntimeException failure) {
					Logger.severe("Mentionator cannot read %s/%s: %s", folder.getFileName(), name, failure.getMessage());
				}
			}

			return files;
		} catch (IOException failure) {
			Logger.severe("Mentionator cannot read the folder %s: %s", folder, failure.getMessage());
			return List.of();
		}
	}

	private static List<String> names(Path folder) throws IOException {
		try (Stream<Path> paths = Files.list(folder)) {
			return paths.filter(Files::isRegularFile)
					.map(path -> path.getFileName().toString())
					.filter(name -> !name.startsWith("."))
					.map(name -> name.contains(".") ? name.substring(0, name.lastIndexOf('.')) : name)
					.filter(name -> !name.isEmpty())
					.distinct()
					.sorted()
					.toList();
		}
	}
}
