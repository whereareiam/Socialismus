package me.whereareiam.socialismus.common.chat.worker.base;

import me.whereareiam.socialismus.common.chat.toggle.InMemoryChatToggleStore;
import me.whereareiam.socialismus.common.config.defaults.chat.ChatMessagesDefaults;
import me.whereareiam.socialismus.common.config.defaults.chat.ChatSettingsDefaults;
import me.whereareiam.socialismus.common.requirement.RequirementEvaluator;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.Worker;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatMessages;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.InternalChat;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.registry.WorkerProcessor;
import me.whereareiam.socialismus.util.EventUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RecipientSelectorTest {
	private final ChatSettings settings = new ChatSettingsDefaults().supply(new ChatSettings());
	private final ChatMessages messages = new ChatMessagesDefaults().supply(new ChatMessages());
	private final InMemoryChatToggleStore toggles = new InMemoryChatToggleStore();
	private final RequirementEvaluator evaluator = mock(RequirementEvaluator.class);
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
		new RecipientSelector(processor, evaluator, () -> settings, () -> messages, toggles);
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
		when(evaluator.check(settings.getToggle().getBypass(), alice)).thenReturn(true);

		assertEquals(Set.of(alice, bob, carol), recipientsOf(chat(true)));
	}

	@Test
	void reachesEveryoneWhenChatsCannotBeTurnedOff() {
		toggles.setOff(bob.getUniqueId(), "global", true);
		settings.getToggle().setEnabled(false);

		assertEquals(Set.of(alice, bob, carol), recipientsOf(chat(true)));
	}

	private Set<SocialismusPlayer> recipientsOf(InternalChat chat) {
		ChatMessage message = ChatMessage.builder()
				.sender(alice)
				.recipients(new HashSet<>(Set.of(alice, bob, carol)))
				.chat(chat)
				.build();

		return Set.copyOf(selector.apply(message).getRecipients());
	}

	private static InternalChat chat(boolean toggleable) {
		return InternalChat.from(Chat.builder()
				.id("global")
				.enabled(true)
				.triggers(List.of())
				.formats(List.of())
				.requirements(Map.of())
				.toggleable(toggleable)
				.build());
	}

	private static SocialismusPlayer player(String name) {
		SocialismusPlayer player = mock(SocialismusPlayer.class);
		when(player.getUsername()).thenReturn(name);
		when(player.getUniqueId()).thenReturn(UUID.nameUUIDFromBytes(name.getBytes()));
		return player;
	}
}
