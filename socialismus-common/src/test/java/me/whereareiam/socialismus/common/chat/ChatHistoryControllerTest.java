package me.whereareiam.socialismus.common.chat;

import me.whereareiam.socialismus.common.chat.broadcast.ChatBroadcaster;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.chat.history.ChatHistoryRemoveByIdEvent;
import me.whereareiam.socialismus.event.chat.history.ChatHistoryRemoveByPlayerEvent;
import me.whereareiam.socialismus.event.chat.history.ChatHistoryRemoveEvent;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.history.HistorySelection;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.service.PlatformInteractor;
import me.whereareiam.socialismus.service.container.ChatHistoryContainerService;
import me.whereareiam.socialismus.type.BroadcastTarget;
import me.whereareiam.socialismus.util.EventUtil;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChatHistoryControllerTest {
	private final ChatHistoryContainerService container = mock(ChatHistoryContainerService.class);
	private final ChatSettings settings = mock(ChatSettings.class, RETURNS_DEEP_STUBS);
	private final PlatformInteractor interactor = mock(PlatformInteractor.class);
	private final EventManager eventManager = mock(EventManager.class);
	private final ChatHistoryController controller = new ChatHistoryController(
			container, () -> settings, mock(ChatBroadcaster.class), interactor
	);

	@BeforeEach
	void setUp() {
		EventUtil.initialize(eventManager);
	}

	@Test
	void aRemovalCallsItsEventAndShowsTheHistoryLeft() {
		when(container.removeMessage(7)).thenReturn(true);

		int removed = controller.remove(HistorySelection.message(7));

		assertEquals(1, removed);
		ArgumentCaptor<ChatHistoryRemoveEvent> event = ArgumentCaptor.forClass(ChatHistoryRemoveEvent.class);
		verify(eventManager).call(event.capture());
		assertEquals(7, ((ChatHistoryRemoveByIdEvent) event.getValue()).getId());
		verify(interactor).broadcast(any(Component.class), eq(BroadcastTarget.PLAYERS));
	}

	@Test
	void theEventCarriesTheVerySelectionThatWasRemoved() {
		when(container.removeMessages(5)).thenReturn(5);
		HistorySelection selection = HistorySelection.oldest(5);

		int removed = controller.remove(selection);

		assertEquals(5, removed);
		ArgumentCaptor<ChatHistoryRemoveEvent> event = ArgumentCaptor.forClass(ChatHistoryRemoveEvent.class);
		verify(eventManager).call(event.capture());
		assertSame(selection, event.getValue().getSelection());
	}

	@Test
	void removingNothingCallsNoEventAndShowsNothing() {
		int removed = controller.remove(HistorySelection.byName("Alice"));

		assertEquals(0, removed);
		verifyNoInteractions(eventManager, interactor);
	}

	@Test
	void aPlayerSelectionRemovesTheirMessagesAndNamesThemInTheEvent() {
		UUID id = UUID.randomUUID();
		SocialismusPlayer alice = mock(SocialismusPlayer.class);
		when(alice.getUniqueId()).thenReturn(id);
		when(alice.getUsername()).thenReturn("Alice");
		FormattedChatMessage first = FormattedChatMessage.builder().id(1).build();
		FormattedChatMessage second = FormattedChatMessage.builder().id(2).build();
		when(container.getMessages(id)).thenReturn(List.of(first, second));
		when(container.removeMessage(anyInt())).thenReturn(true);

		int removed = controller.remove(HistorySelection.of(alice));

		assertEquals(2, removed);
		ArgumentCaptor<ChatHistoryRemoveEvent> event = ArgumentCaptor.forClass(ChatHistoryRemoveEvent.class);
		verify(eventManager).call(event.capture());
		assertEquals("Alice", ((ChatHistoryRemoveByPlayerEvent) event.getValue()).getUsername());
	}
}
