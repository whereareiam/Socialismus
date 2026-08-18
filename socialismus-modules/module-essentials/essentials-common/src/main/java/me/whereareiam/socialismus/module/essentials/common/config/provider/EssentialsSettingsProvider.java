package me.whereareiam.socialismus.module.essentials.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.module.essentials.api.model.config.EssentialsSettings;
import me.whereareiam.socialismus.module.essentials.common.config.defaults.EssentialsSettingsDefaults;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class EssentialsSettingsProvider extends ConfigProvider<EssentialsSettings> {
	@Inject
	public EssentialsSettingsProvider(
			@Named("workingPath") Path workingPath,
			Registry<Reloadable> reloadableRegistry
	) {
		super(workingPath, "settings", EssentialsSettings.class, reloadableRegistry);
	}

	@Override
	protected Configura configura() {
		return versioned(super.configura().withDefaults(EssentialsSettingsDefaults.class), EssentialsSettings.class);
	}
}
