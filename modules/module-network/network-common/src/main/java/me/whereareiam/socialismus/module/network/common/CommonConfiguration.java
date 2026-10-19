package me.whereareiam.socialismus.module.network.common;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.network.common.config.NetworkSettings;
import me.whereareiam.socialismus.module.network.common.config.provider.NetworkSettingsProvider;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
public class CommonConfiguration extends AbstractModule {
	private final Path workingPath;

	@Override
	protected void configure() {
		bind(NetworkSettingsProvider.class).asEagerSingleton();
		bind(NetworkSettings.class).toProvider(NetworkSettingsProvider.class);
	}

	@Provides
	@Singleton
	@Named("workingPath")
	Path provideWorkingPath() {
		try {
			return Files.createDirectories(workingPath);
		} catch (IOException failure) {
			throw new UncheckedIOException("Failed to create the module's directory " + workingPath, failure);
		}
	}
}
