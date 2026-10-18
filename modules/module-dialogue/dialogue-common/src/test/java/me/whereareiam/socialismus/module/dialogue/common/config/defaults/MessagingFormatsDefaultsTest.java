package me.whereareiam.socialismus.module.dialogue.common.config.defaults;

import me.whereareiam.socialismus.module.dialogue.api.model.DialogueFormat;
import me.whereareiam.socialismus.module.dialogue.api.model.config.MessagingFormats;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessagingFormatsDefaultsTest {
	private final MessagingFormats formats = new MessagingFormatsDefaults().supply(new MessagingFormats());

	@Test
	void aReceivedMessageSuggestsAReplyToItsSender() {
		DialogueFormat received = formats.getReceived().getLast();
		Component parsed = MiniMessage.miniMessage().deserialize(received.getFormat().replace("{senderName}", "Alice"));

		ClickEvent click = firstClick(parsed);
		assertNotNull(click, "The received format has no click: " + received.getFormat());
		assertEquals(ClickEvent.Action.SUGGEST_COMMAND, click.action());
		assertEquals("/message Alice ", ((ClickEvent.Payload.Text) click.payload()).value());
	}

	@Test
	void aReceivedMessagePlaysASound() {
		assertEquals("minecraft:entity.experience_orb.pickup", formats.getReceived().getLast().getAlert().getSound());
	}

	private static ClickEvent firstClick(Component component) {
		if (component.clickEvent() != null) return component.clickEvent();

		for (Component child : component.children()) {
			ClickEvent click = firstClick(child);
			if (click != null) return click;
		}

		return null;
	}
}
