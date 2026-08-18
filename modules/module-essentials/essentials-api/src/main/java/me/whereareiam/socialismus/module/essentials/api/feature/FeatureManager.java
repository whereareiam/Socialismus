package me.whereareiam.socialismus.module.essentials.api.feature;


import me.whereareiam.socialismus.module.essentials.api.model.feature.Feature;

import java.util.Map;

public interface FeatureManager {
	void initializeFeatures();

	Map<String, Feature> getActiveFeatures();
}
