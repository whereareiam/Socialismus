package me.whereareiam.socialismus.module.database.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.configura.feature.polymorphic.PolymorphicFeature;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.module.database.common.config.DatabaseSettings;
import me.whereareiam.socialismus.module.database.common.config.SqliteSettings;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Files;
import java.nio.file.Path;

@Singleton
public class DatabaseSettingsProvider extends ConfigProvider<DatabaseSettings> {
	@Inject
	public DatabaseSettingsProvider(
			@Named("workingPath") Path workingPath,
			Registry<Reloadable> reloadableRegistry
	) {
		super(workingPath, "settings", DatabaseSettings.class, reloadableRegistry);
	}

	@Override
	protected Configura configura() {
		return super.configura().withFeature(PolymorphicFeature.defaults());
	}

	/**
	 * A new installation uses SQLite. An existing file must name a type SocialismusDatabase knows: one it cannot read
	 * stops the module instead of silently opening an empty SQLite database.
	 */
	@Override
	protected Class<? extends DatabaseSettings> resolveType(Path path) {
		if (!Files.exists(Path.of(path + configura().extension()))) return SqliteSettings.class;

		return read(path, DatabaseSettings.class).getClass().asSubclass(DatabaseSettings.class);
	}
}
