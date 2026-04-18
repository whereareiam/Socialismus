package me.whereareiam.socialismus.common.chat.worker.base;

import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.common.chat.processor.ChatMessageProcessor;
import me.whereareiam.socialismus.common.requirement.RequirementEvaluator;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.chat.ChatMessages;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.InternalChat;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.chat.trigger.SymbolChatTrigger;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.service.container.ChatContainerService;
import me.whereareiam.socialismus.type.chat.TriggerType;
import me.whereareiam.socialismus.util.ComponentUtil;
import me.whereareiam.socialismus.util.EventUtil;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChatSelectorTest {
	@Mock
	private ChatContainerService containerService;
	@Mock
	private RequirementEvaluator requirementEvaluator;
	@Mock
	private SocialismusPlayer sender;
	@Mock
	private SerializerEngine serializerEngine;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		Logger.init(mock(LoggingHelper.class));
		EventUtil.initialize(mock(EventManager.class));
		Serializer.initialize(() -> serializerEngine);
		when(sender.getUsername()).thenReturn("TestPlayer");
		when(serializerEngine.serialize(any(me.whereareiam.keystone.Actor.class), anyString()))
				.thenReturn(Component.text("You need to write a message."));
	}

	@Test
	void selectChatCancelsMessageWhenSymbolTriggerConsumesEntireMessageAndNotifiesSender() {
		ChatMessage result = createSelector(true).selectChat(createTriggeredMessage("!"));

		assertTrue(result.isCancelled());
		assertNull(result.getChat());
		assertTrue(ComponentUtil.toPlain(result.getContent()).isEmpty());
		verify(sender).sendMessage(Component.text("You need to write a message."));
	}

	@Test
	void selectChatCancelsMessageWhenSymbolTriggerConsumesEntireMessageWithoutNotificationWhenDisabled() {
		ChatMessage result = createSelector(false).selectChat(createTriggeredMessage("!"));

		assertTrue(result.isCancelled());
		assertNull(result.getChat());
		assertTrue(ComponentUtil.toPlain(result.getContent()).isEmpty());
		verify(sender, never()).sendMessage(any());
	}

	private ChatSelector createSelector(boolean notifyEmptyMessage) {
		InternalChat chat = InternalChat.builder()
				.id("local")
				.priority(100)
				.enabled(true)
				.triggers(List.of(SymbolChatTrigger.builder()
						.type(TriggerType.SYMBOL)
						.strip(true)
						.symbol("!")
						.build()))
				.formats(List.of())
				.vanillaSending(true)
				.build();

		when(containerService.hasChatBySymbol("!")).thenReturn(true);
		when(containerService.getChatBySymbol("!")).thenReturn(List.of(chat));

		ChatSettings settings = new ChatSettings();
		settings.setNotifyEmptyMessage(notifyEmptyMessage);
		ChatSettings.FallbackChatSettings fallback = new ChatSettings.FallbackChatSettings();
		fallback.setEnabled(false);
		settings.setFallback(fallback);

		ChatMessages messages = new ChatMessages();
		messages.setEmptyMessage("{prefix}<white>You need to write a message.");

		return new ChatSelector(
				containerService,
				requirementEvaluator,
				() -> messages,
				() -> settings,
				new ChatMessageProcessor()
		);
	}

	private ChatMessage createTriggeredMessage(String content) {
		return ChatMessage.builder()
				.id(1)
				.sender(sender)
				.recipients(Set.of())
				.content(Component.text(content))
				.cancelled(false)
				.vanillaSending(false)
				.build();
	}
}
