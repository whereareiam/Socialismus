package me.whereareiam.socialismus.config.migration;

import me.whereareiam.strata.MigrationAction;
import me.whereareiam.strata.adapter.configura.ConfigContext;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Removes the {@code _version} that Socialismus wrote at the top of every document before its configuration was
 * migrated through Strata. Only the document a stream keeps its version in may carry {@code _version}; anywhere
 * else it means nothing, and since Strata keeps the key in every document, it would stay there for good.
 * <p>
 * Each stream removes it from the documents it owns, with this as its second migration:
 *
 * <pre>{@code
 * .migration(1, "initial-layout", files -> {})
 * .migration(2, DocumentVersionsMigration.NAME,
 *         new DocumentVersionsMigration(workingPath, extension, List.of("messages", "commands"), List.of("announcements")))
 * }</pre>
 */
public final class DocumentVersionsMigration implements MigrationAction<ConfigContext> {
	/** Name of the migration in a stream's history. */
	public static final String NAME = "document-versions";

	private final Path directory;
	private final String extension;
	private final List<String> documents;
	private final List<String> folders;

	/**
	 * @param directory the configuration directory of the stream
	 * @param extension extension of the documents, with its dot
	 * @param documents documents without their extension, never the one the stream keeps its version in
	 * @param folders   folders whose every document loses its version, such as one holding a file per chat
	 */
	public DocumentVersionsMigration(
			@NotNull Path directory,
			@NotNull String extension,
			@NotNull List<String> documents,
			@NotNull List<String> folders
	) {
		this.directory = directory;
		this.extension = extension;
		this.documents = List.copyOf(documents);
		this.folders = List.copyOf(folders);
	}

	@Override
	public void apply(@NotNull ConfigContext files) throws IOException {
		for (String document : documents)
			files.removeVersion(document + extension);

		for (String folder : folders)
			for (String document : documentsIn(folder))
				files.removeVersion(folder + "/" + document);
	}

	private List<String> documentsIn(String folder) throws IOException {
		Path path = directory.resolve(folder);
		if (!Files.isDirectory(path)) return List.of();

		try (Stream<Path> paths = Files.list(path)) {
			return paths
					.filter(Files::isRegularFile)
					.map(file -> file.getFileName().toString())
					.filter(name -> name.endsWith(extension))
					.sorted()
					.toList();
		}
	}
}
