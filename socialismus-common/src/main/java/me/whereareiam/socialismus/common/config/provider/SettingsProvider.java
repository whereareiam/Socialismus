package me.whereareiam.socialismus.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Config;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.input.registry.Registry;
import me.whereareiam.socialismus.model.config.Settings;
import me.whereareiam.socialismus.common.config.template.SettingsTemplate;

import java.nio.file.Path;

@Singleton
public class SettingsProvider extends DefaultConfigProvider<Settings> {
	@Inject
	public SettingsProvider(@Named("dataPath") Path dataPath, Registry<Reloadable> registry) {
		super(dataPath, registry);
	}

	@Override
	protected Settings load() {
		return Config.update(getBasePath().resolve("settings"), Settings.class);
	}

	@Override
	protected void registerTemplate() {
		Config.registerTemplate(SettingsTemplate.class);
	}
}