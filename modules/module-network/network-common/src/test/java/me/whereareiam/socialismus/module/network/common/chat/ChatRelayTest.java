package me.whereareiam.socialismus.module.network.common.chat;

import me.whereareiam.socialismus.common.config.SerializationServiceAdapter;
import me.whereareiam.socialismus.event.chat.ChatBroadcastEvent;
import me.whereareiam.socialismus.event.chat.history.ChatHistoryRemoveByAmountEvent;
import me.whereareiam.socialismus.model.chat.InternalChat;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.model.chat.trigger.SymbolChatTrigger;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.network.api.NetworkAttributes;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import me.whereareiam.socialismus.module.network.api.model.Origin;
import me.whereareiam.socialismus.module.network.common.TestNetwork;
import me.whereareiam.socialismus.module.network.common.config.NetworkSettings;
import me.whereareiam.socialismus.service.chat.ChatCoordinationService;
import me.whereareiam.socialismus.service.chat.ChatHistoryService;
import me.whereareiam.socialismus.service.container.ChatContainerService;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.whereareiam.socialismus.module.network.common.TestPlayers.player;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChatRelayTest {
	private static final InternalChat GLOBAL = InternalChat.builder().id("global").build();
	private static final InternalChat LOCAL = InternalChat.builder()
			.id("local")
			.triggers(List.of(SymbolChatTrigger.builder().symbol("!").radius(50).build()))
			.build();

	private final TestNetwork network = new TestNetwork(Integer.MAX_VALUE, new SerializationServiceAdapter());
	private final NetworkSettings.Chat settings = settings(true);

	@Test
	void aMessageBroadcastHereIsRelayedToTheOtherNodeInItsFormat() {
		Side lobby = side("lobby");
		Side survival = side("survival");
		SocialismusPlayer alice = player("Alice", "lobby");

		lobby.relay.onChatBroadcast(new ChatBroadcastEvent(message(alice, GLOBAL), false));

		ArgumentCaptor<FormattedChatMessage> relayed = ArgumentCaptor.forClass(FormattedChatMessage.class);
		verify(survival.coordination).relay(relayed.capture());
		FormattedChatMessage received = relayed.getValue();
		assertEquals("Alice", received.getSender().getUsername());
		assertEquals("global", received.getChat().getId());
		assertEquals(Component.text("hi"), received.getContent());
		assertEquals(Component.text("[G] Alice: hi"), received.getFormat());
		assertEquals(Optional.of(Origin.remote(NodeId.of("lobby"))), received.getAttribute(NetworkAttributes.ORIGIN));
		verify(lobby.coordination, never()).relay(any(FormattedChatMessage.class));
	}

	@Test
	void withoutKeepingTheFormatTheOtherNodeFormatsTheMessageItself() {
		settings.setPreserveFormat(false);
		Side lobby = side("lobby");
		Side survival = side("survival");

		lobby.relay.onChatBroadcast(new ChatBroadcastEvent(message(player("Alice", null), GLOBAL), false));

		verify(survival.coordination).relay(any(ChatMessage.class));
		verify(survival.coordination, never()).relay(any(FormattedChatMessage.class));
	}

	@Test
	void aChatWithARadiusStaysOnItsNode() {
		Side lobby = side("lobby");
		Side survival = side("survival");

		lobby.relay.onChatBroadcast(new ChatBroadcastEvent(message(player("Alice", null), LOCAL), false));

		verify(survival.coordination, never()).relay(any(FormattedChatMessage.class));
	}

	@Test
	void aRelayedOrCancelledMessageIsNotSentOn() {
		Side lobby = side("lobby");
		Side survival = side("survival");
		FormattedChatMessage relayed = message(player("Alice", null), GLOBAL);
		relayed.setRelayed(true);

		lobby.relay.onChatBroadcast(new ChatBroadcastEvent(relayed, false));
		lobby.relay.onChatBroadcast(new ChatBroadcastEvent(message(player("Bob", null), GLOBAL), true));

		verify(survival.coordination, never()).relay(any(FormattedChatMessage.class));
	}

	@Test
	void aChatTheOtherNodeDoesNotHaveIsIgnoredThere() {
		Side lobby = side("lobby");
		Side survival = side("survival");
		when(survival.chats.getChat("global")).thenReturn(Optional.empty());

		lobby.relay.onChatBroadcast(new ChatBroadcastEvent(message(player("Alice", null), GLOBAL), false));

		verify(survival.coordination, never()).relay(any(FormattedChatMessage.class));
	}

	@Test
	void clearingTheHistoryHereClearsItOnTheOtherNodeWithoutAnEvent() {
		Side lobby = side("lobby");
		Side survival = side("survival");

		lobby.relay.onRemoveByAmount(new ChatHistoryRemoveByAmountEvent(5));

		verify(survival.history).removeMessages(5, false);
	}

	@Test
	void nothingIsRelayedWhileChatIsOff() {
		settings.setEnabled(false);
		Side lobby = side("lobby");
		Side survival = side("survival");

		lobby.relay.onChatBroadcast(new ChatBroadcastEvent(message(player("Alice", null), GLOBAL), false));
		lobby.relay.onRemoveByAmount(new ChatHistoryRemoveByAmountEvent(5));

		verify(survival.coordination, never()).relay(any(FormattedChatMessage.class));
		verify(survival.history, never()).removeMessages(5, false);
	}

	private Side side(String name) {
		TestNetwork.Node node = network.node(name);
		ChatCoordinationService coordination = mock(ChatCoordinationService.class);
		ChatContainerService chats = mock(ChatContainerService.class);
		ChatHistoryService history = mock(ChatHistoryService.class);
		when(chats.getChat(anyString())).thenAnswer(call -> switch (call.getArgument(0, String.class)) {
			case "global" -> Optional.of(GLOBAL);
			case "local" -> Optional.of(LOCAL);
			default -> Optional.empty();
		});
		ChatRelay relay = new ChatRelay(node.service().replication(), () -> settings, coordination, chats, history, node.service().players());

		return new Side(relay, coordination, chats, history);
	}

	private static FormattedChatMessage message(SocialismusPlayer sender, InternalChat chat) {
		return FormattedChatMessage.builder()
				.id(7)
				.sender(sender)
				.recipients(new ArrayList<>())
				.content(Component.text("hi"))
				.chat(chat)
				.format(Component.text("[G] " + sender.getUsername() + ": hi"))
				.build();
	}

	private static NetworkSettings.Chat settings(boolean enabled) {
		NetworkSettings.Chat chat = new NetworkSettings.Chat();
		chat.setEnabled(enabled);
		chat.setPreserveFormat(true);
		chat.setClearHistory(true);

		return chat;
	}

	private record Side(ChatRelay relay, ChatCoordinationService coordination, ChatContainerService chats, ChatHistoryService history) {
	}
}
