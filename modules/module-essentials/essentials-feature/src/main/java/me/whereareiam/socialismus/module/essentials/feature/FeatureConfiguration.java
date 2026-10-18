package me.whereareiam.socialismus.module.essentials.feature;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

import java.util.Map;

/**
 * The features Essentials knows, by the id under which {@code features/features} enables them. It has none at the
 * moment: private messages became the Dialogue module.
 */
public class FeatureConfiguration extends AbstractModule {
	@Override
	protected void configure() {
		bind(new TypeLiteral<Map<String, Class<? extends Module>>>() {}).toInstance(Map.of());
	}
}
