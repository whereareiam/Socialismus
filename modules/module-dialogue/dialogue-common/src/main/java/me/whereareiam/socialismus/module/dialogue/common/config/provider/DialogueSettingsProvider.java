package me.whereareiam.socialismus.module.dialogue.common.config.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.common.config.defaults.DialogueSettingsDefaults;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class DialogueSettingsProvider extends ConfigProvider<DialogueSettings> {
	@Inject
	public DialogueSettingsProvider(
			@Named("workingPath") Path workingPath,
			Registry<Reloadable> reloadableRegistry
	) {
		super(workingPath, "settings", DialogueSettings.class, reloadableRegistry);
	}

	@Override
	protected Configura configura() {
		return super.configura().withDefaults(DialogueSettingsDefaults.class);
	}
}
