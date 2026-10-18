package me.whereareiam.socialismus.module.dialogue.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.module.dialogue.api.model.DialogueFormat;
import me.whereareiam.socialismus.module.dialogue.api.model.config.MessagingFormats;
import me.whereareiam.socialismus.type.chat.Participants;

import java.util.List;
import java.util.Map;

@Singleton
public class MessagingFormatsDefaults implements DefaultsProvider<MessagingFormats> {
	@Override
	public MessagingFormats supply(MessagingFormats config) {
		// Default values
		config.setSent(List.of(format(
				"<gold><bold>PM</bold> <dark_gray>| <gray>{senderName} <dark_gray>-> <gray>{recipientName}: <white>{message}"
		)));
		config.setReceived(List.of(format(
				"<gold><bold>PM</bold> <dark_gray>| <gray>{senderName} <dark_gray>-> <gray>{recipientName}: <white>{message}"
		)));

		return config;
	}

	private static DialogueFormat format(String format) {
		return new DialogueFormat(format, Map.of(
				Participants.SENDER, DialogueSettingsDefaults.anyone(),
				Participants.RECIPIENT, DialogueSettingsDefaults.anyone()
		));
	}
}
