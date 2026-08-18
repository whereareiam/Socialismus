package me.whereareiam.socialismus.module.essentials.common;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.essentials.api.feature.FeatureManager;
import me.whereareiam.socialismus.module.essentials.api.model.config.EssentialsCommands;
import me.whereareiam.socialismus.module.essentials.api.model.config.EssentialsMessages;
import me.whereareiam.socialismus.module.essentials.api.model.config.EssentialsSettings;
import me.whereareiam.socialismus.module.essentials.api.model.config.FeaturesConfig;
import me.whereareiam.socialismus.module.essentials.common.config.provider.EssentialsCommandsProvider;
import me.whereareiam.socialismus.module.essentials.common.config.provider.EssentialsMessagesProvider;
import me.whereareiam.socialismus.module.essentials.common.config.provider.EssentialsSettingsProvider;
import me.whereareiam.socialismus.module.essentials.common.config.provider.FeaturesProvider;
import me.whereareiam.socialismus.module.essentials.common.feature.DefaultFeatureManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
public class CommonConfiguration extends AbstractModule {
	private final Path workingPath;

	@Override
	protected void configure() {
		requestInjection(this);

		// Configuration
		bind(EssentialsSettingsProvider.class).asEagerSingleton();
		bind(EssentialsSettings.class).toProvider(EssentialsSettingsProvider.class);

		bind(EssentialsMessagesProvider.class).asEagerSingleton();
		bind(EssentialsMessages.class).toProvider(EssentialsMessagesProvider.class);

		bind(EssentialsCommandsProvider.class).asEagerSingleton();
		bind(EssentialsCommands.class).toProvider(EssentialsCommandsProvider.class);

		bind(FeaturesProvider.class).asEagerSingleton();
		bind(FeaturesConfig.class).toProvider(FeaturesProvider.class);

		// General
		bind(FeatureManager.class).to(DefaultFeatureManager.class);
	}

	@Provides
	@Singleton
	@Named("workingPath")
	Path provideWorkingPath() {
		return ensureDirectory(workingPath, "workingPath");
	}

	@Provides
	@Singleton
	@Named("featuresPath")
	Path provideFeaturesPath() {
		return ensureDirectory(workingPath.resolve("features"), "featuresPath");
	}

	private Path ensureDirectory(Path path, String label) {
		try {
			Files.createDirectories(path);
			return path;
		} catch (IOException e) {
			throw new RuntimeException("Failed to create " + label + " directory", e);
		}
	}
}
