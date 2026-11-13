package me.whereareiam.socialismus.common.chat.worker.base;

import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatParameters;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.service.style.StyleService;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StyleReaderTest {
	private static final Component STYLED = Component.text("styled");

	private final SocialismusPlayer sender = mock(SocialismusPlayer.class);
	private final StyleService styles = mock(StyleService.class);

	@SuppressWarnings("unchecked")
	private final StyleReader reader = new StyleReader(mock(WorkerProcessor.class), styles);

	@Test
	void readsWhatTheSenderWroteWithTheStylesOfTheirChat() {
		when(styles.read(sender, "<bold>hi", List.of("basic"))).thenReturn(STYLED);

		ChatMessage message = reader.read(message(chat(List.of("basic")), false));

		assertEquals(STYLED, message.getContent());
	}

	@Test
	void leavesAMessageAsItIsInAChatWithoutStyles() {
		ChatMessage message = reader.read(message(chat(List.of()), false));

		assertEquals(Component.text("<bold>hi"), message.getContent());
		verify(styles, never()).read(any(SocialismusPlayer.class), anyString(), anyList());
	}

	@Test
	void leavesARelayedMessageAsTheServerOfItsSenderReadIt() {
		ChatMessage message = reader.read(message(chat(List.of("basic")), true));

		assertEquals(Component.text("<bold>hi"), message.getContent());
		verify(styles, never()).read(any(SocialismusPlayer.class), anyString(), anyList());
	}

	private static Chat chat(List<String> styles) {
		return Chat.builder()
				.id("global")
				.formats(List.of())
				.parameters(ChatParameters.builder().styles(styles).build())
				.build();
	}

	private ChatMessage message(Chat chat, boolean relayed) {
		ChatMessage message = ChatMessage.builder()
				.id(1)
				.sender(sender)
				.content(Component.text("<bold>hi"))
				.chat(chat)
				.build();
		message.setRelayed(relayed);

		return message;
	}
}
