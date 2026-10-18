package me.whereareiam.socialismus.module.dialogue.common.delivery;

import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.keystone.model.SerializerOptions;
import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.module.dialogue.api.model.DialogueAlert;
import me.whereareiam.socialismus.module.dialogue.api.model.DialogueFormat;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.model.config.MessagingFormats;
import me.whereareiam.socialismus.module.dialogue.common.MessagingRequirements;
import me.whereareiam.socialismus.module.dialogue.common.TestDataBuilder;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;
import me.whereareiam.socialismus.type.chat.Participants;
import me.whereareiam.socialismus.type.requirement.RequirementOperatorType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MessageRendererTest {
	private static final RequirementGroup STAFF = RequirementGroup.builder()
			.operator(RequirementOperatorType.AND)
			.groups(Map.of("socialismus:permission", mock(Requirement.class)))
			.build();

	private final RequirementEvaluatorService evaluator = mock(RequirementEvaluatorService.class);
	private final MessagingFormats formats = new MessagingFormats();
	private final SocialismusPlayer alice = TestDataBuilder.mockPlayer("Alice");
	private final SocialismusPlayer bob = TestDataBuilder.mockPlayer("Bob");

	private MessageRenderer renderer;

	@BeforeEach
	void setUp() {
		SerializerEngine engine = mock(SerializerEngine.class);
		when(engine.serialize(any(SerializerContent.class)))
				.thenAnswer(call -> Component.text(call.getArgument(0, SerializerContent.class).getMessage()));
		when(engine.getPlaceholderFormat()).thenReturn(SerializerOptions.PlaceholderFormat.CURLY_BRACES);
		Serializer.initialize(() -> engine);
		Logger.init(mock(LoggingHelper.class));

		formats.setSent(List.of(new DialogueFormat("sent", null)));
		formats.setReceived(List.of(
				new DialogueFormat("plain", null),
				new DialogueFormat("from staff", Map.of(Participants.SENDER, STAFF)),
				new DialogueFormat("to staff", Map.of(Participants.RECIPIENT, STAFF))
		));

		renderer = new MessageRenderer(
				() -> formats,
				new MessagingRequirements(evaluator, TestDataBuilder::defaultSettings)
		);
	}

	@Test
	void takesTheLastFormatWhoseRequirementsAreMet() {
		when(evaluator.check(eq(STAFF), any())).thenReturn(false);
		when(evaluator.check(STAFF, alice)).thenReturn(true);

		renderer.showReceived(message(), bob);

		assertEquals("from staff", received(bob));
	}

	@Test
	void readsTheRecipientsRequirements() {
		when(evaluator.check(eq(STAFF), any())).thenReturn(true);

		renderer.showReceived(message(), bob);

		assertEquals("to staff", received(bob));
	}

	@Test
	void fallsBackToAFormatWithoutRequirements() {
		when(evaluator.check(eq(STAFF), any())).thenReturn(false);

		renderer.showReceived(message(), bob);

		assertEquals("plain", received(bob));
	}

	@Test
	void showsNothingWhenNoFormatMatches() {
		formats.setSent(List.of(new DialogueFormat("to staff", Map.of(Participants.RECIPIENT, STAFF))));

		renderer.showSent(message(), null);

		verify(alice, never()).sendMessage(any(Component.class));
	}

	@Test
	void showsWhatThePlayerWroteAsWrittenNotAsFormatting() {
		useMiniMessage();
		formats.setReceived(List.of(new DialogueFormat("<gray>{senderName}: <white>{message}", null)));
		String written = "<click:run_command:'/op Alice'>free diamonds</click>";

		renderer.showReceived(TestDataBuilder.messageFrom(alice, "Bob").content(written).build(), bob);

		ArgumentCaptor<Component> shown = ArgumentCaptor.forClass(Component.class);
		verify(bob).sendMessage(shown.capture());
		assertEquals("Alice: " + written, PlainTextComponentSerializer.plainText().serialize(shown.getValue()));
		assertFalse(hasClick(shown.getValue()), "The message carries a click event: " + shown.getValue());
	}

	@Test
	void playsTheAlertOfTheChosenFormatToItsViewer() {
		useMiniMessage();
		Audience audience = mock(Audience.class);
		when(bob.getAudience()).thenReturn(audience);
		DialogueAlert alert = new DialogueAlert();
		alert.setSound("minecraft:entity.experience_orb.pickup");
		alert.setActionBar("from {senderName}: {message}");
		DialogueAlert.Title title = new DialogueAlert.Title();
		title.setTitle("{senderName}");
		alert.setTitle(title);
		formats.setReceived(List.of(new DialogueFormat("plain", null, alert)));

		renderer.showReceived(message(), bob);

		verify(bob).playSound("minecraft:entity.experience_orb.pickup", 1f, 1f);
		ArgumentCaptor<Component> actionBar = ArgumentCaptor.forClass(Component.class);
		verify(audience).sendActionBar(actionBar.capture());
		assertEquals("from Alice: hello", PlainTextComponentSerializer.plainText().serialize(actionBar.getValue()));
		ArgumentCaptor<Title> shown = ArgumentCaptor.forClass(Title.class);
		verify(audience).showTitle(shown.capture());
		assertEquals("Alice", PlainTextComponentSerializer.plainText().serialize(shown.getValue().title()));
	}

	@Test
	void playsNothingForAFormatWithoutAnAlert() {
		renderer.showReceived(message(), bob);

		verify(bob, never()).playSound(any(), anyFloat(), anyFloat());
		verify(bob, never()).getAudience();
	}

	/**
	 * Serializes as Keystone does: fills the placeholders into the text, then parses it as MiniMessage.
	 */
	private static void useMiniMessage() {
		SerializerEngine miniMessage = mock(SerializerEngine.class);
		when(miniMessage.serialize(any(SerializerContent.class))).thenAnswer(call -> {
			SerializerContent content = call.getArgument(0, SerializerContent.class);
			String text = content.getMessage();
			for (Map.Entry<String, String> placeholder : content.getPlaceholders().entrySet())
				text = text.replace("{" + placeholder.getKey() + "}", placeholder.getValue());

			return MiniMessage.miniMessage().deserialize(text);
		});
		when(miniMessage.getPlaceholderFormat()).thenReturn(SerializerOptions.PlaceholderFormat.CURLY_BRACES);
		Serializer.initialize(() -> miniMessage);
	}

	private static boolean hasClick(Component component) {
		if (component.clickEvent() != null) return true;

		return component.children().stream().anyMatch(MessageRendererTest::hasClick);
	}

	private Message message() {
		return TestDataBuilder.messageFrom(alice, "Bob").content("hello").build();
	}

	private static String received(SocialismusPlayer player) {
		ArgumentCaptor<Component> message = ArgumentCaptor.forClass(Component.class);
		verify(player).sendMessage(message.capture());

		return ((TextComponent) message.getValue()).content();
	}
}
