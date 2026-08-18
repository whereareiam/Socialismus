package me.whereareiam.socialismus.module.essentials.feature.dialogue.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.config.DialogueSettings;

@Singleton
public class DialogueSettingsDefaults implements DefaultsProvider<DialogueSettings> {
	@Override
	public DialogueSettings supply(DialogueSettings dialogueSettings) {
		DialogueSettings.Synchronization synchronization = new DialogueSettings.Synchronization();
		synchronization.setEnabled(false);
		dialogueSettings.setSynchronization(synchronization);

		DialogueSettings.MessageHistory messageHistory = new DialogueSettings.MessageHistory();
		messageHistory.setEnabled(true);
		messageHistory.setTtl("PT24H");
		messageHistory.setMaxConversations(10);
		messageHistory.setMaxMessagesPerConversation(50);
		dialogueSettings.setMessageHistory(messageHistory);

		DialogueSettings.Reply reply = new DialogueSettings.Reply();
		reply.setEnabled(true);
		reply.setTimeout("PT5M");
		reply.setShowRecentOnError(true);
		reply.setAllowReplyToOffline(false);
		dialogueSettings.setReply(reply);

		return dialogueSettings;
	}
}
