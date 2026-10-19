package me.whereareiam.socialismus.module.link.common.config.migration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.whereareiam.configura.Configura;
import me.whereareiam.strata.MigrationStream;
import me.whereareiam.strata.adapter.configura.ConfigContext;
import me.whereareiam.strata.adapter.configura.ConfiguraTarget;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The configuration migrations of SocialismusLink, which bring the documents of an older installation in its
 * working directory into the layout this build reads: {@code settings}. The module runs them when it loads, before
 * any document is read, and the number of the last one is kept as {@code _version} at the top of {@code settings}.
 * <p>
 * The first release has none. To add one, append it with the next number and never change or renumber one that was
 * released.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LinkConfigMigrations {
	/** Name under which Strata tracks these migrations; it never changes. */
	public static final String ID = "socialismus/module/link/config";

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
		return MigrationStream.<ConfigContext>builder()
				.id(ID)
				.target(new ConfiguraTarget(configura, workingPath, VERSION_DOCUMENT + configura.extension()))
				.build();
	}
}
