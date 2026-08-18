package me.whereareiam.socialismus.module.essentials.feature;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.DialogueConfiguration;

import java.util.Map;

public class FeatureConfiguration extends AbstractModule {
	@Override
	protected void configure() {
		bind(new TypeLiteral<Map<String, Class<? extends Module>>>() {}).toInstance(Map.of(
				"dialogue", DialogueConfiguration.class
		));
	}
}
