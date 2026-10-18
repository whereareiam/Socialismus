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
import org.junit.jupiter.api.Test;

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
	void aMessageFromAnotherServerFiresNoReceivedEvent() {
		ChatMessage fromElsewhere = ChatMessage.builder().origin("lobby").cancelled(true).build();
		when(chatMessageProcessor.process(fromElsewhere)).thenReturn(fromElsewhere);

		coordinator.coordinate(fromElsewhere);

		verify(eventManager, never()).call(any(ChatReceivedEvent.class));
	}
}
