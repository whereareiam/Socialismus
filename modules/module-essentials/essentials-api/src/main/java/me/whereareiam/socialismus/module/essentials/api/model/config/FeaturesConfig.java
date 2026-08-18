package me.whereareiam.socialismus.module.essentials.api.model.config;

import lombok.Getter;
import lombok.Setter;
import me.whereareiam.socialismus.module.essentials.api.model.feature.Feature;

import java.util.Map;

@Getter
@Setter
public class FeaturesConfig {
	private Map<String, Feature> features;
}
