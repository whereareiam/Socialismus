package me.whereareiam.socialismus.module.database;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.RequiredArgsConstructor;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.exception.ModuleLifecycleException;
import me.whereareiam.socialismus.model.resource.ProvidedResource;
import me.whereareiam.socialismus.module.ModuleContext;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.module.database.api.DatabaseResources;
import me.whereareiam.socialismus.module.database.common.CommonConfiguration;
import me.whereareiam.socialismus.module.database.common.DefaultDatabaseService;
import me.whereareiam.socialismus.module.database.common.config.DatabaseSettings;
import me.whereareiam.socialismus.module.database.common.config.migration.DatabaseConfigMigrations;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.resource.ResourceProvider;
import me.whereareiam.strata.Strata;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;

/**
 * Opens the database its settings choose and registers it as Socialismus's database resource, for the modules that
 * keep their data in it. The database is opened when the module is enabled, unregistered when it is disabled and closed then.
 */
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SocialismusDatabase implements SocialisticModule, ResourceProvider {
	private final Registry<Reloadable> reloadableRegistry;
	private DefaultDatabaseService database;

	@Override
	public void enable(@NotNull ModuleContext context) {
		Path dataFolder = context.dataFolder();
		new Strata(List.of(DatabaseConfigMigrations.on(Config.configured(), dataFolder))).migrate();

		Injector injector = Guice.createInjector(
				new SocialismusDatabaseInjectorConfiguration(reloadableRegistry),
				new CommonConfiguration(dataFolder)
		);
		DatabaseSettings settings = injector.getInstance(DatabaseSettings.class);
		context.libraries().load(settings.getType().driverGroup());

		try {
			database = DefaultDatabaseService.open(settings, dataFolder);
		} catch (Exception failure) {
			throw new ModuleLifecycleException("Could not open the " + settings.getType() + " database: "
					+ failure.getMessage(), failure);
		}
	}

	@Override
	public void disable() {
		// Socialismus has unregistered the database by now, so no module reaches for it while it closes.
		if (database != null) database.close();
	}

	@Override
	public List<ProvidedResource<?>> provideResources() {
		return List.of(ProvidedResource.of(DatabaseResources.DATABASE, database));
	}
}
