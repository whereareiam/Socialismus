package me.whereareiam.socialismus.common.chat.worker.base;

import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.keystone.model.SerializerOptions;
import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.common.chat.processor.ChatMessageProcessor;
import me.whereareiam.socialismus.common.requirement.RequirementEvaluator;
import me.whereareiam.socialismus.common.requirement.RequirementReasonResolver;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.chat.ChatMessages;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.InternalChat;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.chat.trigger.SymbolChatTrigger;
import me.whereareiam.socialismus.model.config.message.Messages;
import me.whereareiam.socialismus.model.config.message.RequirementReasons;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementDenial;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.model.requirement.RequirementKey;
import me.whereareiam.socialismus.model.requirement.RequirementResult;
import me.whereareiam.socialismus.service.container.ChatContainerService;
import me.whereareiam.socialismus.type.chat.Participants;
import me.whereareiam.socialismus.type.chat.TriggerType;
import me.whereareiam.socialismus.type.requirement.RequirementFailure;
import me.whereareiam.socialismus.type.requirement.RequirementOperatorType;
import me.whereareiam.socialismus.util.ComponentUtil;
import me.whereareiam.socialismus.util.EventUtil;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChatSelectorTest {
	private static final String NO_CHAT = "No chat takes this message";

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
		RequirementGroup staff = requirementsOf("staff");
		when(requirementEvaluator.evaluate(staff, sender)).thenReturn(deniedBy("staff"));

		ChatMessage result = createSelector(symbolChat("staff", "#", staff), false).selectChat(createTriggeredMessage("#hello"));

		assertTrue(result.isCancelled());
		verify(sender).sendMessage(Component.text("Only staff write in staff chat."));
		verify(sender, never()).sendMessage(Component.text(NO_CHAT));
	}

	@Test
	void selectChatSendsTheNoChatNoticeWhenTheRefusalGivesNoReason() {
		RequirementGroup staff = requirementsOf("staff");
		when(requirementEvaluator.evaluate(staff, sender)).thenReturn(RequirementResult.denied());

		ChatMessage result = createSelector(symbolChat("staff", "#", staff), false).selectChat(createTriggeredMessage("#hello"));

		assertTrue(result.isCancelled());
		verify(sender).sendMessage(Component.text(NO_CHAT));
	}

	@Test
	void selectChatSendsARefusedMessageToTheFallbackWithoutTellingTheReason() {
		RequirementGroup staff = requirementsOf("staff");
		when(requirementEvaluator.evaluate(staff, sender)).thenReturn(deniedBy("staff"));
		InternalChat fallback = symbolChat("fallback", "", null);
		when(containerService.getChat("fallback")).thenReturn(Optional.of(fallback));

		ChatMessage result = createSelector(symbolChat("staff", "#", staff), true).selectChat(createTriggeredMessage("#hello"));

		assertFalse(result.isCancelled());
		assertEquals(fallback, result.getChat());
		verify(sender, never()).sendMessage(any());
	}

	@Test
	void selectChatTellsTheReasonOfTheFallbackWhenNoChatMatched() {
		RequirementGroup overworld = requirementsOf("overworld");
		when(requirementEvaluator.evaluate(overworld, sender)).thenReturn(deniedBy("overworld"));
		when(containerService.getChat("fallback")).thenReturn(Optional.of(symbolChat("fallback", "", overworld)));

		ChatMessage result = createSelector(symbolChat("staff", "#", null), true).selectChat(createTriggeredMessage("hello"));

		assertTrue(result.isCancelled());
		verify(sender).sendMessage(Component.text("fallback chat only works in the overworld."));
	}

	private ChatSelector createSelector(InternalChat chat, boolean fallbackEnabled) {
		// A chat without sender requirements takes everyone, as the evaluator makes of a missing group.
		when(requirementEvaluator.evaluate(isNull(), any())).thenReturn(RequirementResult.passed());
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
		messages.setRequirements(Map.of(Participants.SENDER, Map.of(
				"test:staff", required("Only staff write in {chat} chat."),
				"test:overworld", required("{chat} chat only works in the overworld.")
		)));
		Messages core = new Messages();
		core.setRequirements(Map.of());

		return new ChatSelector(
				containerService,
				requirementEvaluator,
				new RequirementReasonResolver(() -> core),
				() -> messages,
				() -> settings,
				new ChatMessageProcessor()
		);
	}

	private static RequirementResult deniedBy(String type) {
		return RequirementResult.denied(RequirementDenial.of(
				RequirementKey.create("test", type, Requirement.class),
				RequirementFailure.REQUIRED,
				Map.of()
		));
	}

	private static RequirementReasons required(String message) {
		RequirementReasons reasons = new RequirementReasons();
		reasons.setRequired(message);

		return reasons;
	}

	private static String render(SerializerContent content) {
		String message = content.getMessage();
		for (Map.Entry<String, String> placeholder : content.getPlaceholders().entrySet())
			message = message.replace("{" + placeholder.getKey() + "}", placeholder.getValue());

		return message;
	}

	private static InternalChat symbolChat(String id, String symbol, RequirementGroup sender) {
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
				.requirements(sender == null ? Map.of() : Map.of(Participants.SENDER, sender))
				.vanillaSending(true)
				.build();
	}

	private static RequirementGroup requirementsOf(String name) {
		return RequirementGroup.builder()
				.operator(RequirementOperatorType.AND)
				.groups(Map.of("test:" + name, new Requirement()))
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
				requirementEvaluator,
				new RequirementReasonResolver(Messages::new),
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
