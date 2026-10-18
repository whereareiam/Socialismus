package me.whereareiam.socialismus.common.config.migration;

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
 * The migrations of Socialismus's configuration, which bring the documents of an older installation into the layout
 * this build reads. They cover the settings, messages, commands, sync and updater documents and everything under
 * {@code chat/}, and {@code chats/} where earlier builds kept the chats; every module migrates its own working
 * directory. On startup the ones an installation has not seen yet run in order, before any document is loaded, and
 * the number of the last one is kept as {@code _version} at the top of the settings document.
 * <p>
 * To add a migration, append it with the next number and never change or renumber one that was released. A
 * migration with real logic becomes a class of its own that is listed here.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SocialismusConfigMigrations {
	/** Name under which Strata tracks these migrations; it never changes. */
	public static final String ID = "socialismus/config";

	private static final String VERSION_DOCUMENT = "settings";

	/**
	 * Declares the migrations for a data directory.
	 *
	 * @param configura reads and writes the documents; it must have a {@code StrataFeature}, which keeps
	 *                  {@code _version} in a document when Socialismus rewrites it
	 * @param dataPath  Socialismus's data directory
	 * @return migrations to run through Strata before any document is loaded
	 */
	public static @NotNull MigrationStream<ConfigContext> on(@NotNull Configura configura, @NotNull Path dataPath) {
		String extension = configura.extension();
		String versionFile = VERSION_DOCUMENT + extension;

		return MigrationStream.<ConfigContext>builder()
				.id(ID)
				.target(new ConfiguraTarget(configura, dataPath, versionFile))
				// A directory without settings is a new installation, whose documents are about to be written in
				// their current layout, so nothing has to run.
				.baseline((files, latest) -> files.exists(versionFile) ? OptionalInt.of(0) : OptionalInt.empty())
				// Changes nothing: the builds before migrations wrote version 1 into every document themselves.
				.migration(1, "initial-layout", files -> {})
				.migration(2, DocumentVersionsMigration.NAME, new DocumentVersionsMigration(
						dataPath,
						extension,
						List.of("messages", "commands"),
						List.of("chats")
				))
				.migration(ConfigLayoutMigration.VERSION, ConfigLayoutMigration.NAME, new ConfigLayoutMigration(dataPath, extension))
				.migration(ChatHistoryRequirementsMigration.VERSION, ChatHistoryRequirementsMigration.NAME, new ChatHistoryRequirementsMigration(extension))
				.build();
	}
}
