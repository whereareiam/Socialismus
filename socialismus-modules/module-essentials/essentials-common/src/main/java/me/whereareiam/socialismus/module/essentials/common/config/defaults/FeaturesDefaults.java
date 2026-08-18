package me.whereareiam.socialismus.module.essentials.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.module.essentials.api.model.config.FeaturesConfig;
import me.whereareiam.socialismus.module.essentials.api.model.feature.CommandFeature;

import java.util.Map;

@Singleton
public class FeaturesDefaults implements DefaultsProvider<FeaturesConfig> {
	@Override
	public FeaturesConfig supply(FeaturesConfig config) {
		// Default values
		CommandFeature dialogue = CommandFeature.builder()
				.enabled(true)
				.registerCommands(true)
				.build();

		config.setFeatures(Map.of(
				"dialogue", dialogue
		));

		return config;
	}
}
