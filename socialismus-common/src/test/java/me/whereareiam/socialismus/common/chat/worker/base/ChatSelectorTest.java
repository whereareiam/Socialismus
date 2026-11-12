package me.whereareiam.socialismus.common.chat.worker.base;

import me.whereareiam.conditia.result.Reason;
import me.whereareiam.conditia.result.Verdict;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.keystone.model.SerializerOptions;
import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.common.chat.processor.ChatMessageProcessor;
import me.whereareiam.socialismus.common.phrase.DefaultPhraseService;
import me.whereareiam.socialismus.common.requirement.RequirementReasonResolver;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatMessages;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.InternalChat;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.chat.trigger.SymbolChatTrigger;
import me.whereareiam.socialismus.model.phrase.Phrases;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.RequirementParts;
import me.whereareiam.socialismus.service.chat.ChatRequirementService;
import me.whereareiam.socialismus.service.container.ChatContainerService;
import me.whereareiam.socialismus.type.chat.TriggerType;
import me.whereareiam.socialismus.util.ComponentUtil;
import me.whereareiam.socialismus.util.EventUtil;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChatSelectorTest {
	private static final String NO_CHAT = "No chat takes this message";

	@Mock
	private ChatContainerService containerService;
	@Mock
	private ChatRequirementService requirements;
	@Mock
	private SocialismusPlayer sender;
	@Mock
	private SerializerEngine serializerEngine;

	private final Map<String, Verdict> verdicts = new HashMap<>();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		// A chat whose verdict a test does not set takes everyone.
		when(requirements.writes(any(), any())).thenAnswer(invocation ->
				verdicts.getOrDefault(invocation.<Chat>getArgument(0).getId(), new Verdict.Passed()));
		Logger.init(mock(LoggingHelper.class));
		EventUtil.initialize(mock(EventManager.class));
		Serializer.initialize(() -> serializerEngine);
		// The serializer stays initialized for the tests after these, which format placeholders through it.
		when(serializerEngine.getPlaceholderFormat()).thenReturn(SerializerOptions.PlaceholderFormat.CURLY_BRACES);
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

	@Test
	void selectChatTellsTheSenderWhyTheChatTheyWroteForRefusedThem() {
		verdicts.put("staff", failedBy("staff"));

		ChatMessage result = createSelector(symbolChat("staff", "#"), false).selectChat(createTriggeredMessage("#hello"));

		assertTrue(result.isCancelled());
		verify(sender).sendMessage(Component.text("Only staff write in staff chat."));
		verify(sender, never()).sendMessage(Component.text(NO_CHAT));
	}

	@Test
	void selectChatSendsTheNoChatNoticeWhenTheRefusalGivesNoReason() {
		verdicts.put("staff", new Verdict.Failed("chats.staff.requires[0].any", "any", List.of(RequirementParts.SENDER), Optional.empty()));

		ChatMessage result = createSelector(symbolChat("staff", "#"), false).selectChat(createTriggeredMessage("#hello"));

		assertTrue(result.isCancelled());
		verify(sender).sendMessage(Component.text(NO_CHAT));
	}

	@Test
	void selectChatSendsARefusedMessageToTheFallbackWithoutTellingTheReason() {
		verdicts.put("staff", failedBy("staff"));
		InternalChat fallback = symbolChat("fallback", "");
		when(containerService.getChat("fallback")).thenReturn(Optional.of(fallback));

		ChatMessage result = createSelector(symbolChat("staff", "#"), true).selectChat(createTriggeredMessage("#hello"));

		assertFalse(result.isCancelled());
		assertEquals(fallback, result.getChat());
		verify(sender, never()).sendMessage(any());
	}

	@Test
	void selectChatTellsTheReasonOfTheFallbackWhenNoChatMatched() {
		verdicts.put("fallback", failedBy("overworld"));
		when(containerService.getChat("fallback")).thenReturn(Optional.of(symbolChat("fallback", "")));

		ChatMessage result = createSelector(symbolChat("staff", "#"), true).selectChat(createTriggeredMessage("hello"));

		assertTrue(result.isCancelled());
		verify(sender).sendMessage(Component.text("fallback chat only works in the overworld."));
	}

	private ChatSelector createSelector(InternalChat chat, boolean fallbackEnabled) {
		String symbol = ((SymbolChatTrigger) chat.getTriggers().getFirst()).getSymbol();
		when(containerService.hasChatBySymbol(symbol)).thenReturn(true);
		when(containerService.getChatBySymbol(symbol)).thenReturn(List.of(chat));
		when(containerService.getChats()).thenReturn(Set.of(chat));
		when(serializerEngine.serialize(any(SerializerContent.class)))
				.thenAnswer(invocation -> Component.text(render(invocation.getArgument(0))));
		when(serializerEngine.serialize(any(me.whereareiam.keystone.Actor.class), anyString()))
				.thenAnswer(invocation -> Component.text(invocation.<String>getArgument(1)));

		ChatSettings settings = new ChatSettings();
		ChatSettings.Notifications notifications = new ChatSettings.Notifications();
		notifications.setNoChat(true);
		settings.setNotifications(notifications);
		ChatSettings.FallbackChatSettings fallback = new ChatSettings.FallbackChatSettings();
		fallback.setEnabled(fallbackEnabled);
		fallback.setChat("fallback");
		settings.setFallback(fallback);

		ChatMessages messages = new ChatMessages();
		messages.setNoChatMatch(NO_CHAT);
		Phrases phrases = Phrases.builder()
				.section("chat", scope -> scope.section("denied", denied -> denied
						.text("staff.refusal", "Only staff write in {chat} chat.")
						.text("overworld.refusal", "{chat} chat only works in the overworld.")))
				.build();

		return new ChatSelector(
				containerService,
				requirements,
				new RequirementReasonResolver(new DefaultPhraseService(() -> phrases)),
				() -> messages,
				() -> settings,
				new ChatMessageProcessor()
		);
	}

	// A failure whose reason's key is "<type>.refusal".
	private static Verdict failedBy(String type) {
		return new Verdict.Failed("chats.test.requires[0]." + type, type, List.of(RequirementParts.SENDER), Optional.of(new Refusal()));
	}

	private static String render(SerializerContent content) {
		String message = content.getMessage();
		for (Map.Entry<String, String> placeholder : content.getPlaceholders().entrySet())
			message = message.replace("{" + placeholder.getKey() + "}", placeholder.getValue());

		return message;
	}

	private static InternalChat symbolChat(String id, String symbol) {
		return InternalChat.builder()
				.id(id)
				.priority(1)
				.enabled(true)
				.triggers(List.of(SymbolChatTrigger.builder()
						.type(TriggerType.SYMBOL)
						.strip(true)
						.symbol(symbol)
						.build()))
				.formats(List.of())
				.vanillaSending(true)
				.build();
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
		ChatSettings.Notifications notifications = new ChatSettings.Notifications();
		notifications.setEmptyMessage(notifyEmptyMessage);
		settings.setNotifications(notifications);
		ChatSettings.FallbackChatSettings fallback = new ChatSettings.FallbackChatSettings();
		fallback.setEnabled(false);
		settings.setFallback(fallback);

		ChatMessages messages = new ChatMessages();
		messages.setEmptyMessage("{prefix}<white>You need to write a message.");

		return new ChatSelector(
				containerService,
				requirements,
				new RequirementReasonResolver(new DefaultPhraseService(Phrases::none)),
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

	private record Refusal() implements Reason {
	}
}
