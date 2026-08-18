package me.whereareiam.socialismus.module.essentials.api.feature;

import me.whereareiam.socialismus.module.essentials.api.model.feature.Feature;

public interface FeatureInitializer<T extends Feature> {
	String getId();

	void initialize(T feature);
}
