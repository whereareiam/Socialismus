package me.whereareiam.socialismus.module.essentials.feature.dialogue.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.config.DialogueSettings;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.defaults.DialogueSettingsDefaults;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class DialogueSettingsProvider extends ConfigProvider<DialogueSettings> {
	@Inject
	public DialogueSettingsProvider(
			@Named("featurePath") Path featurePath,
			Registry<Reloadable> reloadableRegistry
	) {
		super(featurePath, "settings", DialogueSettings.class, reloadableRegistry);
	}

	@Override
	protected Configura configura() {
		return versioned(super.configura().withDefaults(DialogueSettingsDefaults.class), DialogueSettings.class);
	}
}
