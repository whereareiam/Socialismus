package me.whereareiam.socialismus.module.essentials.feature.dialogue.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.config.DialogueMessages;

@Singleton
public class DialogueMessagesDefaults implements DefaultsProvider<DialogueMessages> {
	@Override
	public DialogueMessages supply(DialogueMessages dialogueMessages) {
		// Default values
		DialogueMessages.Commands commands = new DialogueMessages.Commands();

		DialogueMessages.Commands.Message message = new DialogueMessages.Commands.Message();
		message.setNoRecipient("{prefix}<white>Recipient not found");
		message.setSamePlayer("{prefix}<white>You cannot send a message to yourself");
		message.setSenderFormat("<gold><bold>PM</bold> <dark_gray>| <gray>{senderName} <dark_gray>-> <gray>{recipientName}: <white>{message}");
		message.setRecipientFormat("<gold><bold>PM</bold> <dark_gray>| <gray>{senderName} <dark_gray>-> <gray>{recipientName}: <white>{message}");
		commands.setMessage(message);

		DialogueMessages.Commands.Reply reply = new DialogueMessages.Commands.Reply();
		reply.setNoPreviousSender("{prefix}<white>No previous sender found.");
		reply.setNoConversations("{prefix}<white>No conversations found.");
		reply.setConversationListHeader("{prefix}<white>Recent conversations:");
		reply.setConversationListEntry("<gray>{index}. <white>{player}");
		commands.setReply(reply);

		dialogueMessages.setCommands(commands);

		return dialogueMessages;
	}
}
