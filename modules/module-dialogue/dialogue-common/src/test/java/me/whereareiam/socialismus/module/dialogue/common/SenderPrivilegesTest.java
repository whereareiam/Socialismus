package me.whereareiam.socialismus.module.dialogue.common;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.service.style.StyleService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SenderPrivilegesTest {
	private final DialogueSettings settings = TestDataBuilder.defaultSettings();
	private final StyleService styles = mock(StyleService.class);
	private final SenderPrivileges privileges = new SenderPrivileges(DialogueConditions.bound(settings), () -> settings, styles);
	private final SocialismusPlayer alice = TestDataBuilder.mockPlayer("Alice");

	@Test
	void theMessageCarriesTheStyleTheSenderGetsOnTheirServer() {
		settings.getMessaging().setStyles(List.of("basic", "vip"));
		when(styles.styleOf(alice, List.of("basic", "vip"))).thenReturn(Optional.of("vip"));
		Message message = TestDataBuilder.messageFrom(alice, "Bob").build();

		privileges.grant(message);

		assertEquals("vip", message.getStyle());
	}

	@Test
	void aSenderWithoutAStyleSendsPlainText() {
		when(styles.styleOf(alice, settings.getMessaging().getStyles())).thenReturn(Optional.empty());
		Message message = TestDataBuilder.messageFrom(alice, "Bob").build();

		privileges.grant(message);

		assertNull(message.getStyle());
	}
}
