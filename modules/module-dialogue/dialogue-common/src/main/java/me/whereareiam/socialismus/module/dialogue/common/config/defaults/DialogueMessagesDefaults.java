package me.whereareiam.socialismus.module.dialogue.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueMessages;

@Singleton
public class DialogueMessagesDefaults implements DefaultsProvider<DialogueMessages> {
	@Override
	public DialogueMessages supply(DialogueMessages config) {
		// Default values
		DialogueMessages.Messaging.Reply reply = new DialogueMessages.Messaging.Reply();
		reply.setNoConversations("{prefix}<white>You have no conversation to reply to.");
		reply.setNoPartner("{prefix}<white>Your conversations are too old to reply to. Recent conversations:");
		reply.setPartnerOffline("{prefix}<white>{partner} is no longer online.");
		reply.setConversation("<gray>{index}. <white>{partner}");

		DialogueMessages.Messaging messaging = new DialogueMessages.Messaging();
		messaging.setNoRecipient("{prefix}<white>Recipient not found");
		messaging.setSamePlayer("{prefix}<white>You cannot send a message to yourself");
		messaging.setNotAllowed("{prefix}<white>You cannot send a message to {recipientName}");
		messaging.setReply(reply);

		config.setMessaging(messaging);

		return config;
	}
}
