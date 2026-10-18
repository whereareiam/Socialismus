package me.whereareiam.socialismus.module.dialogue.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.module.dialogue.api.model.DialogueAlert;
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
		// Clicking a received message puts a reply to its sender into the chat input.
		DialogueAlert alert = new DialogueAlert();
		alert.setSound("minecraft:entity.experience_orb.pickup");
		config.setReceived(List.of(new DialogueFormat(
				"<click:suggest_command:'/message {senderName} '><hover:show_text:'<gray>Click to reply'>"
						+ "<gold><bold>PM</bold> <dark_gray>| <gray>{senderName} <dark_gray>-> <gray>{recipientName}: <white>{message}"
						+ "</hover></click>",
				anyone(),
				alert
		)));

		return config;
	}

	private static DialogueFormat format(String format) {
		return new DialogueFormat(format, anyone());
	}

	private static Map<Participants, RequirementGroup> anyone() {
		return Map.of(
				Participants.SENDER, DialogueSettingsDefaults.anyone(),
				Participants.RECIPIENT, DialogueSettingsDefaults.anyone()
		);
	}
}
