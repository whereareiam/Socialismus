package me.whereareiam.socialismus.module.dialogue.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.module.dialogue.api.model.DialogueFormat;
import me.whereareiam.socialismus.module.dialogue.api.model.config.ModerationFormats;

import java.util.List;

@Singleton
public class ModerationFormatsDefaults implements DefaultsProvider<ModerationFormats> {
	@Override
	public ModerationFormats supply(ModerationFormats config) {
		config.setSpy(List.of(new DialogueFormat(
				"<dark_gray>[<gray>Spy</gray>] {senderName} -> {recipientName}: <gray>{message}",
				null
		)));

		return config;
	}
}
