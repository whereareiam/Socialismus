package me.whereareiam.socialismus.common.chat.worker.base;

import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatParameters;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.service.chat.ChatConditionService;
import me.whereareiam.socialismus.service.style.TextStyle;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StyleReaderTest {
	private static final Component STYLED = Component.text("styled");

	private final SocialismusPlayer sender = mock(SocialismusPlayer.class);
	private final ChatConditionService conditions = mock(ChatConditionService.class);

	@SuppressWarnings("unchecked")
	private final StyleReader reader = new StyleReader(mock(WorkerProcessor.class), conditions);

	@Test
	void readsWhatTheSenderWroteWithTheStyleTheirChatGivesThem() {
		Chat chat = chat(List.of("basic"));
		when(conditions.style(chat, sender)).thenReturn(new Styled());

		ChatMessage message = reader.read(message(chat, false));

		assertEquals(STYLED, message.getContent());
	}

	@Test
	void leavesAMessageAsItIsInAChatWithoutStyles() {
		ChatMessage message = reader.read(message(chat(List.of()), false));

		assertEquals(Component.text("<bold>hi"), message.getContent());
		verify(conditions, never()).style(any(), any());
	}

	@Test
	void leavesTheMessageOfASenderHostedElsewhereAsTheirServerReadIt() {
		ChatMessage message = reader.read(message(chat(List.of("basic")), true));

		assertEquals(Component.text("<bold>hi"), message.getContent());
		verify(conditions, never()).style(any(), any());
	}

	private static Chat chat(List<String> styles) {
		return Chat.builder()
				.id("global")
				.formats(List.of())
				.parameters(ChatParameters.builder().styles(styles).build())
				.build();
	}

	private ChatMessage message(Chat chat, boolean hostedElsewhere) {
		when(sender.isHostedHere()).thenReturn(!hostedElsewhere);

		return ChatMessage.builder()
				.id(1)
				.sender(sender)
				.content(Component.text("<bold>hi"))
				.chat(chat)
				.build();
	}

	private static final class Styled implements TextStyle {
		@Override
		public @NotNull String id() {
			return "basic";
		}

		@Override
		public @NotNull Component read(@NotNull String text) {
			return text.equals("<bold>hi") ? STYLED : Component.text(text);
		}
	}
}
