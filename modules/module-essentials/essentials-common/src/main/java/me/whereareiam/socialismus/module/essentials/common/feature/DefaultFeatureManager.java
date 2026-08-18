package me.whereareiam.socialismus.module.essentials.common.feature;

import com.google.inject.*;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.google.inject.name.Named;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.essentials.api.feature.FeatureManager;
import me.whereareiam.socialismus.module.essentials.api.model.config.EssentialsSettings;
import me.whereareiam.socialismus.module.essentials.api.model.config.FeaturesConfig;
import me.whereareiam.socialismus.module.essentials.api.model.feature.Feature;
import me.whereareiam.socialismus.module.essentials.api.feature.FeatureInitializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class DefaultFeatureManager implements FeatureManager {
	private final Injector injector;
	private final FeaturesConfig features;
	private final EssentialsSettings settings;

	private final Path featuresPath;

	private final Map<String, Class<? extends Module>> modules;

	private final Map<String, Feature> activeFeatures = new HashMap<>();

	@Inject
	public DefaultFeatureManager(
			Injector injector,
			FeaturesConfig features,
			EssentialsSettings settings,
			@Named("featuresPath") Path featuresPath,
			Map<String, Class<? extends Module>> modules
	) {
		this.injector = injector;
		this.features = features;
		this.settings = settings;
		this.featuresPath = featuresPath;
		this.modules = modules;
	}

	@Override
	public void initializeFeatures() {
		var features = this.features.getFeatures();

		boolean announce = settings.getAnnounce().isFeatureInitialization();

		for (Map.Entry<String, ? extends Feature> entry : features.entrySet()) {
			String id = entry.getKey();
			Feature feat = entry.getValue();

			if (!feat.isEnabled()) continue;

			if (modules.containsKey(id)) {
				Class<? extends Module> moduleClass = modules.get(id);

				Path featurePath = featuresPath.resolve(id);
				try {
					Files.createDirectories(featurePath);
				} catch (IOException ex) {
					Logger.severe("Cannot create directory for feature " + id, ex);
					continue;
				}

				try {
					Injector featureInjector = injector.createChildInjector(
							moduleClass.getDeclaredConstructor().newInstance(),
							new AbstractModule() {
								@Override
								protected void configure() {
									bind(Path.class).annotatedWith(Names.named("featurePath")).toInstance(featurePath);
								}
							}
					);
					FeatureInitializer<? extends Feature> init = featureInjector.getInstance(new Key<>() {});

					invokeInit(init, feat);
					if (announce) Logger.info("Feature enabled: %s", id);

					activeFeatures.put(id, feat);
				} catch (Exception e) {
					Logger.severe("Failed to initialize feature: " + id, e);
				}
			}
		}
	}

	@Override
	public Map<String, Feature> getActiveFeatures() {
		return activeFeatures;
	}

	@SuppressWarnings("unchecked")
	private <F extends Feature> void invokeInit(FeatureInitializer<F> init, Feature f) {
		init.initialize((F) f);
	}
}
