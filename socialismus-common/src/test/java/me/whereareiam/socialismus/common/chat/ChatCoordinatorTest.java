package me.whereareiam.socialismus.common.chat;

import me.whereareiam.socialismus.common.chat.broadcast.ChatBroadcastPolicy;
import me.whereareiam.socialismus.common.chat.broadcast.ChatBroadcaster;
import me.whereareiam.socialismus.common.chat.processor.ChatMessageProcessor;
import me.whereareiam.socialismus.common.chat.processor.FormattedChatMessageProcessor;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.chat.ChatReceivedEvent;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.service.container.ChatHistoryContainerService;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChatCoordinatorTest {
	private final ChatMessageProcessor chatMessageProcessor = mock(ChatMessageProcessor.class);
	private final EventManager eventManager = mock(EventManager.class);
	private final ChatCoordinator coordinator = new ChatCoordinator(
			mock(FormattedChatMessageProcessor.class),
			mock(ChatHistoryContainerService.class),
			chatMessageProcessor,
			mock(ChatBroadcaster.class),
			mock(ChatBroadcastPolicy.class),
			eventManager
	);

	@Test
	void aCancelledReceivedEventTakesTheMessageOutOfChat() {
		doAnswer(call -> {
			call.getArgument(0, ChatReceivedEvent.class).setCancelled(true);
			return null;
		}).when(eventManager).call(any(ChatReceivedEvent.class));

		FormattedChatMessage result = coordinator.coordinate(ChatMessage.builder().build());

		assertTrue(result.isCancelled());
		verifyNoInteractions(chatMessageProcessor);
	}

	@Test
	void aRelayedMessageFiresNoReceivedEventAndIsMarkedRelayed() {
		ChatMessage fromElsewhere = ChatMessage.builder().cancelled(true).build();
		when(chatMessageProcessor.process(fromElsewhere)).thenReturn(fromElsewhere);

		coordinator.relay(fromElsewhere);

		verify(eventManager, never()).call(any(ChatReceivedEvent.class));
		assertTrue(fromElsewhere.isRelayed());
	}

	@Test
	void aRelayedFormattedMessageKeepsItsFormat() {
		Component format = Component.text("[G] Alice: hi");
		FormattedChatMessage relayed = FormattedChatMessage.builder().format(format).build();
		// Cancelled by the processor, so the test stops before the broadcast, which needs the event manager.
		FormattedChatMessage processed = FormattedChatMessage.builder().cancelled(true).build();
		when(chatMessageProcessor.process(relayed)).thenReturn(processed);

		coordinator.relay(relayed);

		assertTrue(relayed.isRelayed());
		assertEquals(format, processed.getFormat());
	}
}
