package me.whereareiam.socialismus.module.essentials.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.module.essentials.api.model.config.FeaturesConfig;

import java.util.Map;

@Singleton
public class FeaturesDefaults implements DefaultsProvider<FeaturesConfig> {
	@Override
	public FeaturesConfig supply(FeaturesConfig config) {
		// Default values: Essentials has no feature at the moment
		config.setFeatures(Map.of());

		return config;
	}
}
