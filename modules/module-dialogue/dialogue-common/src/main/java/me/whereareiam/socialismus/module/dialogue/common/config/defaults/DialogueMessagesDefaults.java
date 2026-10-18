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

		DialogueMessages.Messaging.ConversationMode conversationMode = new DialogueMessages.Messaging.ConversationMode();
		conversationMode.setEntered("{prefix}<white>You now write to <gray>{recipientName}</gray> in chat; <gray>/message {recipientName}</gray> again to stop.");
		conversationMode.setUnavailable("{prefix}<white>Write a message: <gray>/message <player> <message>");
		conversationMode.setLeft("{prefix}<white>You no longer write to <gray>{recipientName}</gray> in chat.");

		DialogueMessages.Messaging messaging = new DialogueMessages.Messaging();
		messaging.setNoRecipient("{prefix}<white>Recipient not found");
		messaging.setSamePlayer("{prefix}<white>You cannot send a message to yourself");
		messaging.setNotAllowed("{prefix}<white>You cannot send a message to {recipientName}");
		messaging.setReply(reply);
		messaging.setConversationMode(conversationMode);

		config.setMessaging(messaging);

		DialogueMessages.Control.Toggle toggle = new DialogueMessages.Control.Toggle();
		toggle.setOff("{prefix}<white>You no longer receive private messages.");
		toggle.setOn("{prefix}<white>You receive private messages again.");
		toggle.setBlocked("{prefix}<gray>{recipientName}</gray> <white>does not receive private messages.");
		toggle.setUnavailable("{prefix}<white>Private messages cannot be turned off here.");

		DialogueMessages.Control.Ignore.Listing listing = new DialogueMessages.Control.Ignore.Listing();
		listing.setHeader("{prefix}<white>You ignore:");
		listing.setEntry("<gray>- <white>{ignoredName}");
		listing.setEmpty("{prefix}<white>You ignore nobody.");

		DialogueMessages.Control.Ignore ignore = new DialogueMessages.Control.Ignore();
		ignore.setAdded("{prefix}<white>You now ignore <gray>{ignoredName}</gray>.");
		ignore.setRemoved("{prefix}<white>You no longer ignore <gray>{ignoredName}</gray>.");
		ignore.setBlocked("{prefix}<gray>{recipientName}</gray> <white>does not receive your private messages.");
		ignore.setExempt("{prefix}<gray>{ignoredName}</gray> <white>cannot be ignored.");
		ignore.setUnavailable("{prefix}<white>Players cannot be ignored here.");
		ignore.setList(listing);

		DialogueMessages.Control control = new DialogueMessages.Control();
		control.setToggle(toggle);
		control.setIgnore(ignore);
		config.setControl(control);

		DialogueMessages.Moderation.Spy spy = new DialogueMessages.Moderation.Spy();
		spy.setOn("{prefix}<white>You now see the private messages of others.");
		spy.setOff("{prefix}<white>You no longer see the private messages of others.");
		spy.setUnavailable("{prefix}<white>Private messages cannot be seen by others here.");

		DialogueMessages.Moderation moderation = new DialogueMessages.Moderation();
		moderation.setSpy(spy);
		config.setModeration(moderation);

		return config;
	}
}
