package me.whereareiam.socialismus.module.essentials.common.config.migration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.config.migration.DocumentVersionsMigration;
import me.whereareiam.strata.MigrationStream;
import me.whereareiam.strata.adapter.configura.ConfigContext;
import me.whereareiam.strata.adapter.configura.ConfiguraTarget;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.OptionalInt;

/**
 * The configuration migrations of Essentials, which bring the documents of an older installation in its working
 * directory into the layout this build reads: {@code settings}, {@code messages}, {@code commands} and everything
 * under {@code features/} and {@code features/dialogue/}. The module runs them when it loads, before any document is
 * read, and the number of the last one is kept as {@code _version} at the top of {@code settings}.
 * <p>
 * To add a migration, append it with the next number and never change or renumber one that was released.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EssentialsConfigMigrations {
	/** Name under which Strata tracks these migrations; it never changes. */
	public static final String ID = "socialismus/module/essentials/config";

	private static final String VERSION_DOCUMENT = "settings";

	/**
	 * Declares the migrations for the module's working directory.
	 *
	 * @param configura   reads and writes the documents; it must have a {@code StrataFeature}, as Socialismus's
	 *                    shared instance does
	 * @param workingPath the module's working directory
	 * @return migrations to run through Strata before any document is loaded
	 */
	public static @NotNull MigrationStream<ConfigContext> on(@NotNull Configura configura, @NotNull Path workingPath) {
		String extension = configura.extension();
		String versionFile = VERSION_DOCUMENT + extension;

		return MigrationStream.<ConfigContext>builder()
				.id(ID)
				.target(new ConfiguraTarget(configura, workingPath, versionFile))
				// A directory without {@code settings} is a new installation, whose documents are about to be written in
				// their current layout, so nothing has to run.
				.baseline((files, latest) -> files.exists(versionFile) ? OptionalInt.of(0) : OptionalInt.empty())
				// Changes nothing: the builds before migrations wrote version 1 into every document themselves.
				.migration(1, "initial-layout", files -> {})
				.migration(2, DocumentVersionsMigration.NAME, new DocumentVersionsMigration(
						workingPath,
						extension,
						List.of("messages", "commands"),
						List.of("features", "features/dialogue")
				))
				.build();
	}
}
