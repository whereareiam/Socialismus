package me.whereareiam.socialismus.adapter.config.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.socialismus.adapter.config.management.DefaultConfigurationLoader;
import me.whereareiam.socialismus.api.Reloadable;
import me.whereareiam.socialismus.api.input.registry.Registry;
import me.whereareiam.socialismus.api.model.config.Settings;
import me.whereareiam.socialismus.shared.Constants;

import java.nio.file.Path;

@Singleton
public class SettingsProvider implements Provider<Settings>, Reloadable {
	private final Path dataPath;
	private final DefaultConfigurationLoader defaultConfigurationLoader;

	private Settings settings;

	@Inject
	public SettingsProvider(@Named("dataPath") Path dataPath, DefaultConfigurationLoader defaultConfigurationLoader, Registry<Reloadable> registry) {
		this.dataPath = dataPath;
		this.defaultConfigurationLoader = defaultConfigurationLoader;

		registry.register(this);
	}

	@Override
	public Settings get() {
		if (settings != null) return settings;

		load();

		return settings;
	}

	@Override
	public void reload() {
		load();
	}

	private void load() {
		settings = defaultConfigurationLoader.load(dataPath.resolve("settings"), Settings.class);
		Constants.IDENTIFIER = settings.getSynchronization().getServer();
	}
}