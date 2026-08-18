package me.whereareiam.socialismus.module.essentials.feature.dialogue.defaults;

import me.whereareiam.socialismus.module.essentials.feature.dialogue.config.DialogueMessages;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DialogueMessagesDefaultsTest {
	@Test
	void suppliesNestedCommandMessagesForEmptyConfiguration() {
		DialogueMessages messages = new DialogueMessagesDefaults().supply(new DialogueMessages());

		assertNotNull(messages.getCommands());
		assertNotNull(messages.getCommands().getMessage());
		assertNotNull(messages.getCommands().getReply());
	}
}
