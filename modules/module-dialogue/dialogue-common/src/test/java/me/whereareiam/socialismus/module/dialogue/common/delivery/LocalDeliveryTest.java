package me.whereareiam.socialismus.module.dialogue.common.delivery;

import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.DialogueFormat;
import me.whereareiam.socialismus.module.dialogue.api.model.config.MessagingFormats;
import me.whereareiam.socialismus.module.dialogue.common.MessagingRequirements;
import me.whereareiam.socialismus.module.dialogue.common.TestDataBuilder;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Local delivery")
class LocalDeliveryTest {
	private final PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
	private final SocialismusPlayer alice = TestDataBuilder.mockPlayer("Alice");
	private final SocialismusPlayer bob = TestDataBuilder.mockPlayer("Bob");

	private LocalDelivery delivery;

	@BeforeEach
	void setUp() {
		SerializerEngine engine = mock(SerializerEngine.class);
		when(engine.serialize(any(SerializerContent.class))).thenAnswer(call -> {
			SerializerContent content = call.getArgument(0, SerializerContent.class);
			String rendered = content.getMessage();
			for (Map.Entry<String, String> placeholder : content.getPlaceholders().entrySet())
				rendered = rendered.replace("{" + placeholder.getKey() + "}", placeholder.getValue());

			return Component.text(rendered);
		});
		Serializer.initialize(() -> engine);

		MessagingFormats formats = new MessagingFormats();
		formats.setSent(List.of(new DialogueFormat("To {recipientName}: {message}", null)));
		formats.setReceived(List.of(new DialogueFormat("{senderName} -> {recipientName}: {message}", null)));

		MessagingRequirements requirements = new MessagingRequirements(
				mock(RequirementEvaluatorService.class),
				TestDataBuilder::defaultSettings
		);
		delivery = new LocalDelivery(playerRegistry, new MessageRenderer(() -> formats, requirements));
	}

	@DisplayName("Fills the names and the message into both formats")
	@Test
	void fillsBothFormats() {
		when(playerRegistry.getPlayerData("Bob")).thenReturn(Optional.of(bob));

		delivery.deliver(TestDataBuilder.messageFrom(alice, "Bob").content("hello").build(), true);

		assertEquals("To Bob: hello", received(alice));
		assertEquals("Alice -> Bob: hello", received(bob));
	}

	@DisplayName("Shows a message from another server only to its recipient")
	@Test
	void showsAMessageFromAnotherServerOnlyToTheRecipient() {
		when(playerRegistry.getPlayerData("Bob")).thenReturn(Optional.of(bob));

		delivery.deliver(TestDataBuilder.messageFrom(alice, "Bob").content("hello").build(), false);

		verify(alice, never()).sendMessage(any(Component.class));
		assertEquals("Alice -> Bob: hello", received(bob));
	}

	@DisplayName("Leaves a recipient who is not on this server to the network")
	@Test
	void leavesAMissingRecipient() {
		when(playerRegistry.getPlayerData("Bob")).thenReturn(Optional.empty());

		assertFalse(delivery.deliver(TestDataBuilder.messageFrom(alice, "Bob").build(), true));
		verify(alice, never()).sendMessage(any(Component.class));
	}

	private static String received(SocialismusPlayer player) {
		ArgumentCaptor<Component> message = ArgumentCaptor.forClass(Component.class);
		verify(player).sendMessage(message.capture());

		return ((TextComponent) message.getValue()).content();
	}
}
