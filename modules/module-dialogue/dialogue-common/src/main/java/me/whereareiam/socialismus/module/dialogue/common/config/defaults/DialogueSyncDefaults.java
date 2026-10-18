package me.whereareiam.socialismus.module.dialogue.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSync;

import java.time.Duration;

@Singleton
public class DialogueSyncDefaults implements DefaultsProvider<DialogueSync> {
	@Override
	public DialogueSync supply(DialogueSync config) {
		// Default values
		config.setEnabled(true);
		config.setConfirmation(Duration.ofSeconds(3));

		return config;
	}
}
