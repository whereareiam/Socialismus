package me.whereareiam.socialismus.module.essentials;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.RequiredArgsConstructor;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.module.SocialisticModule;
import me.whereareiam.socialismus.module.essentials.api.feature.FeatureManager;
import me.whereareiam.socialismus.module.essentials.command.CommandRegistrar;
import me.whereareiam.socialismus.module.essentials.common.CommonConfiguration;
import me.whereareiam.socialismus.module.essentials.common.config.migration.EssentialsConfigMigrations;
import me.whereareiam.socialismus.module.essentials.feature.FeatureConfiguration;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.CommandService;
import me.whereareiam.socialismus.service.PlatformInteractor;
import me.whereareiam.socialismus.service.SerializationService;
import me.whereareiam.strata.Strata;

import java.util.List;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class Essentials extends SocialisticModule {
	private final Registry<Reloadable> reloadableRegistry;

	// Services from parent injector
	private final CommandService commandService;
	private final PlayerRegistry playerRegistry;
	private final PlatformInteractor platformInteractor;
	private final SerializationService serializationService;

	private Injector injector;

	@Override
	public void onLoad() {
		new Strata(List.of(EssentialsConfigMigrations.on(Config.configured(), workingPath))).migrate();

		injector =
				Guice.createInjector(
						new EssentialsInjectorConfiguration(
								reloadableRegistry,
								commandService,
								playerRegistry,
								platformInteractor,
								serializationService),
						new CommonConfiguration(workingPath),
						new FeatureConfiguration());
	}

	@Override
	public void onEnable() {
		injector.getInstance(CommandRegistrar.class).registerCommands();
		injector.getInstance(FeatureManager.class).initializeFeatures();
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onUnload() {
	}
}
