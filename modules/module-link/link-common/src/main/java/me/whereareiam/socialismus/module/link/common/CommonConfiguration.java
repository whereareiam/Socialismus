package me.whereareiam.socialismus.module.link.common;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.module.link.common.config.LinkSettings;
import me.whereareiam.socialismus.module.link.common.config.provider.LinkSettingsProvider;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
public class CommonConfiguration extends AbstractModule {
	private final Path workingPath;

	@Override
	protected void configure() {
		bind(LinkSettingsProvider.class).asEagerSingleton();
		bind(LinkSettings.class).toProvider(LinkSettingsProvider.class);
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
