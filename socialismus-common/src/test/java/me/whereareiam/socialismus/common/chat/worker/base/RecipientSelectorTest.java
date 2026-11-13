package me.whereareiam.socialismus.common.chat.worker.base;

import me.whereareiam.conditia.model.Part;
import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.socialismus.common.chat.toggle.InMemoryChatToggleStore;
import me.whereareiam.socialismus.common.config.defaults.chat.ChatSettingsDefaults;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatParameters;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.InternalChat;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.service.chat.ChatConditions;
import me.whereareiam.socialismus.service.chat.ChatPlaces;
import me.whereareiam.socialismus.service.chat.ChatToggleConditions;
import me.whereareiam.socialismus.util.EventUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RecipientSelectorTest {
	private final ChatSettings settings = new ChatSettingsDefaults().supply(new ChatSettings());
	private final InMemoryChatToggleStore toggles = new InMemoryChatToggleStore();
	private final ChatConditions conditions = mock(ChatConditions.class);
	private final ChatPlaces places = mock(ChatPlaces.class);
	private final ChatToggleConditions toggleConditions = mock(ChatToggleConditions.class);
	private final NobodyNotice nobody = mock(NobodyNotice.class);
	private final SocialismusPlayer alice = player("Alice");
	private final SocialismusPlayer bob = player("Bob");
	private final SocialismusPlayer carol = player("Carol");

	private Function<ChatMessage, ChatMessage> selector;

	@BeforeEach
	@SuppressWarnings("unchecked")
	void setUp() {
		Logger.init(mock(LoggingHelper.class));
		EventUtil.initialize(mock(EventManager.class));
		WorkerProcessor<ChatMessage> processor = mock(WorkerProcessor.class);
		when(conditions.of(any())).thenReturn(Optional.of(places));
		when(places.reaches(any(), any())).thenReturn(new Verdict.Passed());
		new RecipientSelector(processor, conditions, toggleConditions, () -> settings, toggles, nobody);
		ArgumentCaptor<Worker<ChatMessage>> worker = ArgumentCaptor.forClass(Worker.class);
		verify(processor).addWorker(worker.capture());
		selector = worker.getValue().getFunction();
	}

	@Test
	void keepsAChatFromThePlayersWhoTurnedItOff() {
		toggles.setOff(bob.getUniqueId(), "global", true);

		assertEquals(Set.of(alice, carol), recipientsOf(chat(true)));
	}

	@Test
	void reachesEveryoneInAChatThatCannotBeTurnedOff() {
		toggles.setOff(bob.getUniqueId(), "global", true);

		assertEquals(Set.of(alice, bob, carol), recipientsOf(chat(false)));
	}

	@Test
	void aSenderWhoBypassesItReachesThePlayersWhoTurnedItOff() {
		toggles.setOff(bob.getUniqueId(), "global", true);
		when(toggleConditions.bypasses(alice)).thenReturn(true);

		assertEquals(Set.of(alice, bob, carol), recipientsOf(chat(true)));
	}

	@Test
	void keepsAChatFromThePlayersItDoesNotReach() {
		when(places.reaches(eq(carol), any())).thenReturn(refused("chats.global.reaches[0].nearby"));

		assertEquals(Set.of(alice, bob), recipientsOf(chat(true)));
	}

	@Test
	void aMessageThatReachesOnlyItsSenderIsKeptBack_AndTheSenderToldWhy() {
		settings.getNotifications().setNobody(true);
		Verdict.Failed far = refused("chats.global.reaches[0].nearby");
		when(places.reaches(eq(bob), any())).thenReturn(far);
		when(places.reaches(eq(carol), any())).thenReturn(far);

		ChatMessage message = selector.apply(message(chat(true)));

		assertTrue(message.isCancelled());
		verify(nobody).tell(alice, message.getChat(), List.of(far, far));
	}

	@Test
	void withoutTheNoticeAMessageThatReachesOnlyItsSenderGoesToTheSender() {
		when(places.reaches(eq(bob), any())).thenReturn(refused("chats.global.reaches[0].nearby"));
		when(places.reaches(eq(carol), any())).thenReturn(refused("chats.global.reaches[0].nearby"));

		ChatMessage message = selector.apply(message(chat(true)));

		assertFalse(message.isCancelled());
		assertEquals(Set.of(alice), Set.copyOf(message.getRecipients()));
		verify(nobody, never()).tell(any(), any(), any());
	}

	@Test
	void aRelayedMessageThatReachesNobodyHereTellsNobody() {
		settings.getNotifications().setNobody(true);
		when(places.reaches(any(), any())).thenReturn(refused("chats.global.reaches[0].nearby"));
		when(places.reaches(eq(alice), any())).thenReturn(new Verdict.Passed());
		ChatMessage relayed = message(chat(true));
		relayed.setRelayed(true);

		assertTrue(selector.apply(relayed).isCancelled());
		verify(nobody, never()).tell(any(), any(), any());
	}

	@Test
	void aChatWithoutPlacesReachesNobody() {
		when(conditions.of(any())).thenReturn(Optional.empty());

		assertTrue(selector.apply(message(chat(true))).isCancelled());
	}

	private static Verdict.Failed refused(String path) {
		return new Verdict.Failed(path, "nearby", List.<Part<?>>of(), Optional.empty());
	}

	@Test
	void reachesEveryoneWhenChatsCannotBeTurnedOff() {
		toggles.setOff(bob.getUniqueId(), "global", true);
		settings.getToggle().setEnabled(false);

		assertEquals(Set.of(alice, bob, carol), recipientsOf(chat(true)));
	}

	private Set<SocialismusPlayer> recipientsOf(InternalChat chat) {
		return Set.copyOf(selector.apply(message(chat)).getRecipients());
	}

	private ChatMessage message(InternalChat chat) {
		return ChatMessage.builder()
				.sender(alice)
				.recipients(new HashSet<>(Set.of(alice, bob, carol)))
				.chat(chat)
				.build();
	}

	private static InternalChat chat(boolean toggleable) {
		return InternalChat.from(Chat.builder()
				.id("global")
				.enabled(true)
				.triggers(List.of())
				.formats(List.of())
				.parameters(ChatParameters.builder().toggleable(toggleable).build())
				.build());
	}

	private static SocialismusPlayer player(String name) {
		SocialismusPlayer player = mock(SocialismusPlayer.class);
		when(player.getUsername()).thenReturn(name);
		when(player.getUniqueId()).thenReturn(UUID.nameUUIDFromBytes(name.getBytes()));
		return player;
	}
}
