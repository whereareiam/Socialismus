package me.whereareiam.socialismus.module.dialogue.common;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.service.style.StylePlace;
import me.whereareiam.socialismus.service.style.Styles;
import me.whereareiam.socialismus.service.style.TextStyle;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SenderPrivilegesTest {
	private final DialogueSettings settings = TestDataBuilder.defaultSettings();
	private final SocialismusPlayer alice = TestDataBuilder.mockPlayer("Alice");

	@Test
	void theMessageCarriesTheStyleTheSenderGetsOnTheirServer() {
		settings.getMessaging().setStyles(List.of("basic", "vip"));
		TextStyle vip = mock(TextStyle.class);
		when(vip.id()).thenReturn("vip");
		Message message = TestDataBuilder.messageFrom(alice, "Bob").build();

		privileges(vip).grant(message);

		assertEquals("vip", message.getStyle());
	}

	@Test
	void aSenderWithoutAStyleSendsPlainText() {
		Message message = TestDataBuilder.messageFrom(alice, "Bob").build();

		privileges(TextStyle.PLAIN).grant(message);

		assertEquals("plain", message.getStyle());
	}

	private SenderPrivileges privileges(TextStyle style) {
		StylePlace place = mock(StylePlace.class);
		when(place.of(alice)).thenReturn(style);
		Styles styles = mock(Styles.class);
		when(styles.place(eq("messaging.styles"), any())).thenReturn(place);

		return new SenderPrivileges(TestDialogueConditions.bound(settings, styles));
	}
}
