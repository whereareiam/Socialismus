package me.whereareiam.socialismus.module.dialogue.common.delivery;

import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.requirement.Requirement;
import me.whereareiam.socialismus.model.requirement.RequirementGroup;
import me.whereareiam.socialismus.module.dialogue.api.model.DialogueFormat;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.model.config.MessagingFormats;
import me.whereareiam.socialismus.module.dialogue.common.MessagingRequirements;
import me.whereareiam.socialismus.module.dialogue.common.TestDataBuilder;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;
import me.whereareiam.socialismus.type.chat.Participants;
import me.whereareiam.socialismus.type.requirement.RequirementOperatorType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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

	private Message message() {
		return TestDataBuilder.messageFrom(alice, "Bob").content("hello").build();
	}

	private static String received(SocialismusPlayer player) {
		ArgumentCaptor<Component> message = ArgumentCaptor.forClass(Component.class);
		verify(player).sendMessage(message.capture());

		return ((TextComponent) message.getValue()).content();
	}
}
