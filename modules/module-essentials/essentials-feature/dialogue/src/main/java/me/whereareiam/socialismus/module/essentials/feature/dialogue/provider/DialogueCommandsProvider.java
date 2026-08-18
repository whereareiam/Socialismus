package me.whereareiam.socialismus.module.essentials.feature.dialogue.provider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import me.whereareiam.configura.Configura;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.config.ConfigProvider;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.config.DialogueCommands;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.defaults.DialogueCommandsDefaults;
import me.whereareiam.socialismus.registry.base.Registry;

import java.nio.file.Path;

@Singleton
public class DialogueCommandsProvider extends ConfigProvider<DialogueCommands> {
	@Inject
	public DialogueCommandsProvider(
			@Named("featurePath") Path featurePath,
			Registry<Reloadable> reloadableRegistry
	) {
		super(featurePath, "commands", DialogueCommands.class, reloadableRegistry);
	}

	@Override
	protected Configura configura() {
		return versioned(super.configura().withDefaults(DialogueCommandsDefaults.class), DialogueCommands.class);
	}
}
