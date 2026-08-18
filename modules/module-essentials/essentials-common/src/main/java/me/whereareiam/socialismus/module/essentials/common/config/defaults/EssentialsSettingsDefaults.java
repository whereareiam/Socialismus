package me.whereareiam.socialismus.module.essentials.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.module.essentials.api.model.config.EssentialsSettings;

@Singleton
public class EssentialsSettingsDefaults implements DefaultsProvider<EssentialsSettings> {
	@Override
	public EssentialsSettings supply(EssentialsSettings config) {
		// Default values
		EssentialsSettings.Announce announce = new EssentialsSettings.Announce();
		announce.setFeatureInitialization(true);

		config.setAnnounce(announce);

		return config;
	}
}
