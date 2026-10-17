package me.whereareiam.socialismus.module.essentials.common.config.migration;

import me.whereareiam.strata.MigrationAction;
import me.whereareiam.strata.adapter.configura.ConfigContext;
import org.jetbrains.annotations.NotNull;

/**
 * Renames the {@code synchronization} section of the dialogue feature's settings to {@code sync}, as Socialismus
 * names it.
 */
final class DialogueSyncMigration implements MigrationAction<ConfigContext> {
	static final int VERSION = 3;
	static final String NAME = "dialogue-sync";

	private final String settings;

	DialogueSyncMigration(@NotNull String extension) {
		this.settings = "features/dialogue/settings" + extension;
	}

	@Override
	public void apply(@NotNull ConfigContext files) {
		files.rename(settings, "/synchronization", "/sync");
	}
}
