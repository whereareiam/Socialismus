package me.whereareiam.socialismus.module.dialogue.common.delivery;

import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.keystone.model.SerializerOptions;
import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.event.DialogueDeliveredEvent;
import me.whereareiam.socialismus.module.dialogue.api.model.DialogueFormat;
import me.whereareiam.socialismus.module.dialogue.api.model.IgnoredPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.model.config.MessagingFormats;
import me.whereareiam.socialismus.module.dialogue.api.model.config.ModerationFormats;
import me.whereareiam.socialismus.module.dialogue.api.type.DeliveryStatus;
import me.whereareiam.socialismus.module.dialogue.common.MessagingRequirements;
import me.whereareiam.socialismus.module.dialogue.common.TestDataBuilder;
import me.whereareiam.socialismus.module.dialogue.common.control.InMemoryIgnoreStore;
import me.whereareiam.socialismus.module.dialogue.common.control.InMemoryMessageToggleStore;
import me.whereareiam.socialismus.module.dialogue.common.control.RecipientControl;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.service.requirement.RequirementEvaluatorService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Local delivery")
class LocalDeliveryTest {
	private final PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
	private final EventManager eventManager = mock(EventManager.class);
	private final InMemoryMessageToggleStore toggles = new InMemoryMessageToggleStore();
	private final InMemoryIgnoreStore ignores = new InMemoryIgnoreStore();
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
		when(engine.getPlaceholderFormat()).thenReturn(SerializerOptions.PlaceholderFormat.CURLY_BRACES);
		Serializer.initialize(() -> engine);

		MessagingFormats formats = new MessagingFormats();
		formats.setSent(List.of(new DialogueFormat("To {recipientName}: {message}", null)));
		formats.setReceived(List.of(new DialogueFormat("{senderName} -> {recipientName}: {message}", null)));

		MessagingRequirements requirements = new MessagingRequirements(
				mock(RequirementEvaluatorService.class),
				TestDataBuilder::defaultSettings
		);
		RecipientControl control = new RecipientControl(TestDataBuilder::defaultSettings, toggles, ignores);
		delivery = new LocalDelivery(playerRegistry, new MessageRenderer(() -> formats, ModerationFormats::new, requirements), eventManager, control);
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

	@DisplayName("Tells the add-ons a message reached its recipient")
	@Test
	void announcesTheDelivery() {
		when(playerRegistry.getPlayerData("Bob")).thenReturn(Optional.of(bob));
		Message message = TestDataBuilder.messageFrom(alice, "Bob").content("hello").build();

		delivery.deliver(message, false);

		ArgumentCaptor<DialogueDeliveredEvent> event = ArgumentCaptor.forClass(DialogueDeliveredEvent.class);
		verify(eventManager).call(event.capture());
		assertSame(message, event.getValue().getMessage());
		assertSame(bob, event.getValue().getRecipient());
	}

	@DisplayName("Keeps a message from a recipient who turned private messages off")
	@Test
	void refusesWhenTheRecipientTurnedMessagesOff() {
		when(playerRegistry.getPlayerData("Bob")).thenReturn(Optional.of(bob));
		toggles.setOff(bob.getUniqueId(), true);

		assertEquals(DeliveryStatus.MESSAGES_OFF, delivery.deliver(TestDataBuilder.messageFrom(alice, "Bob").build(), true));
		verify(bob, never()).sendMessage(any(Component.class));
		verify(alice, never()).sendMessage(any(Component.class));
	}

	@DisplayName("Lets a sender who bypasses it reach a recipient who turned messages off")
	@Test
	void deliversWhenTheSenderBypassesTheToggle() {
		when(playerRegistry.getPlayerData("Bob")).thenReturn(Optional.of(bob));
		toggles.setOff(bob.getUniqueId(), true);

		assertEquals(DeliveryStatus.DELIVERED_LOCALLY,
				delivery.deliver(TestDataBuilder.messageFrom(alice, "Bob").bypassesToggle(true).build(), true));
	}

	@DisplayName("Keeps a message from a recipient who ignores the sender")
	@Test
	void refusesWhenTheRecipientIgnoresTheSender() {
		when(playerRegistry.getPlayerData("Bob")).thenReturn(Optional.of(bob));
		ignores.ignore(bob.getUniqueId(), new IgnoredPlayer(alice.getUniqueId(), "Alice"));

		assertEquals(DeliveryStatus.IGNORED, delivery.deliver(TestDataBuilder.messageFrom(alice, "Bob").build(), true));
		verify(bob, never()).sendMessage(any(Component.class));
	}

	@DisplayName("Delivers the message of a sender who cannot be ignored")
	@Test
	void deliversWhenTheSenderIsExemptFromIgnoring() {
		when(playerRegistry.getPlayerData("Bob")).thenReturn(Optional.of(bob));
		ignores.ignore(bob.getUniqueId(), new IgnoredPlayer(alice.getUniqueId(), "Alice"));

		assertEquals(DeliveryStatus.DELIVERED_LOCALLY,
				delivery.deliver(TestDataBuilder.messageFrom(alice, "Bob").exemptFromIgnore(true).build(), true));
	}

	@DisplayName("Leaves a recipient who is not on this server to the network")
	@Test
	void leavesAMissingRecipient() {
		when(playerRegistry.getPlayerData("Bob")).thenReturn(Optional.empty());

		assertEquals(DeliveryStatus.RECIPIENT_OFFLINE, delivery.deliver(TestDataBuilder.messageFrom(alice, "Bob").build(), true));
		verify(alice, never()).sendMessage(any(Component.class));
		verifyNoInteractions(eventManager);
	}

	private static String received(SocialismusPlayer player) {
		ArgumentCaptor<Component> message = ArgumentCaptor.forClass(Component.class);
		verify(player).sendMessage(message.capture());

		return PlainTextComponentSerializer.plainText().serialize(message.getValue());
	}
}
