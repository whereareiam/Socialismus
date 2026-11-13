package me.whereareiam.socialismus.module.dialogue.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.model.requirement.RequirementLists;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;

import java.time.Duration;
import java.util.List;

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
		messaging.setReply(reply);
		messaging.setConversations(conversations);
		messaging.setConversationMode(true);
		messaging.setConfirmation(Duration.ofSeconds(3));
		messaging.setStyles(List.of("basic", "full"));

		config.setMessaging(messaging);

		DialogueSettings.Control.Toggle toggle = new DialogueSettings.Control.Toggle();
		toggle.setEnabled(true);
		toggle.setBypass(RequirementLists.permission("socialismus.dialogue.toggle.bypass"));

		DialogueSettings.Control.Ignore ignore = new DialogueSettings.Control.Ignore();
		ignore.setEnabled(true);
		ignore.setIncludesChat(false);
		ignore.setExempt(RequirementLists.permission("socialismus.dialogue.ignore.exempt"));

		DialogueSettings.Control control = new DialogueSettings.Control();
		control.setToggle(toggle);
		control.setIgnore(ignore);
		config.setControl(control);

		DialogueSettings.Moderation.Spy spy = new DialogueSettings.Moderation.Spy();
		spy.setEnabled(true);
		spy.setExempt(RequirementLists.permission("socialismus.dialogue.spy.exempt"));

		DialogueSettings.Moderation moderation = new DialogueSettings.Moderation();
		moderation.setSpy(spy);
		moderation.setPrivacy(false);
		config.setModeration(moderation);

		return config;
	}
}
