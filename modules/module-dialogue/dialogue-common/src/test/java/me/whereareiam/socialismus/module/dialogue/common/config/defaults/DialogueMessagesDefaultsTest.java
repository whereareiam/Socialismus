package me.whereareiam.socialismus.module.dialogue.common.config.defaults;

import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueMessages;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DialogueMessagesDefaultsTest {
	@Test
	void suppliesTheMessagesOfEveryAreaForEmptyConfiguration() {
		DialogueMessages messages = new DialogueMessagesDefaults().supply(new DialogueMessages());

		assertNotNull(messages.getMessaging());
		assertNotNull(messages.getMessaging().getNoRecipient());
		assertNotNull(messages.getMessaging().getReply());
		assertNotNull(messages.getMessaging().getReply().getConversation());
	}
}
