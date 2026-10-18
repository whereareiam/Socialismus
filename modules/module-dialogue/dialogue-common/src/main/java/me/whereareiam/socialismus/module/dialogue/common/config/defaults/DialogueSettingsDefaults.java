package me.whereareiam.socialismus.module.dialogue.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.type.chat.Participants;
import me.whereareiam.socialismus.type.requirement.RequirementOperatorType;

import java.time.Duration;
import java.util.Map;

@Singleton
public class DialogueSettingsDefaults implements DefaultsProvider<DialogueSettings> {
	@Override
	public DialogueSettings supply(DialogueSettings config) {
		// Default values
		DialogueSettings.Messaging.Reply reply = new DialogueSettings.Messaging.Reply();
		reply.setTimeout(Duration.ofMinutes(5));
		reply.setToOffline(false);

		DialogueSettings.Messaging.Conversations conversations = new DialogueSettings.Messaging.Conversations();
		conversations.setTtl(Duration.ofHours(24));
		conversations.setMaxConversations(10);
		conversations.setMaxMessages(50);

		DialogueSettings.Messaging messaging = new DialogueSettings.Messaging();
		messaging.setRequirements(Map.of(
				Participants.SENDER, anyone(),
				Participants.RECIPIENT, anyone()
		));
		messaging.setReply(reply);
		messaging.setConversations(conversations);

		config.setMessaging(messaging);

		return config;
	}

	static RequirementGroup anyone() {
		return RequirementGroup.builder()
				.operator(RequirementOperatorType.AND)
				.groups(Map.of())
				.build();
	}
}
