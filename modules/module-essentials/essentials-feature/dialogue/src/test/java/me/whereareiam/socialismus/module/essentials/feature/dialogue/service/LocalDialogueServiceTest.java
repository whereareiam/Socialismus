package me.whereareiam.socialismus.module.essentials.feature.dialogue.service;

import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.TestDataBuilder;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.config.DialogueMessages;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.defaults.DialogueMessagesDefaults;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.dialogue.type.LocalDialogueService;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Local dialogue service")
class LocalDialogueServiceTest {
	private final PlayerRegistry playerRegistry = mock(PlayerRegistry.class);
	private final SocialismusPlayer alice = TestDataBuilder.mockPlayer("Alice");
	private final SocialismusPlayer bob = TestDataBuilder.mockPlayer("Bob");

	private LocalDialogueService service;

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

		DialogueMessages messages = new DialogueMessagesDefaults().supply(new DialogueMessages());
		messages.getCommands().getMessage().setSenderFormat("To {recipientName}: {message}");
		messages.getCommands().getMessage().setRecipientFormat("{senderName} -> {recipientName}: {message}");
		messages.getCommands().getMessage().setNoRecipient("{recipientName} is not online");

		service = new LocalDialogueService(playerRegistry, () -> messages);
	}

	@DisplayName("Fills the names and the message into both formats")
	@Test
	void fillsBothFormats() {
		when(playerRegistry.getPlayerData("Alice")).thenReturn(Optional.of(alice));
		when(playerRegistry.getPlayerData("Bob")).thenReturn(Optional.of(bob));

		service.deliverLocally(TestDataBuilder.createDialogue(alice, "Bob", "hello"));

		assertEquals("To Bob: hello", received(alice));
		assertEquals("Alice -> Bob: hello", received(bob));
	}

	@DisplayName("Names the missing recipient to the sender")
	@Test
	void namesTheMissingRecipient() {
		when(playerRegistry.getPlayerData("Alice")).thenReturn(Optional.of(alice));

		service.notifyUndeliverable(TestDataBuilder.createDialogue(alice, "Bob", "hello"));

		assertEquals("Bob is not online", received(alice));
	}

	private static String received(SocialismusPlayer player) {
		ArgumentCaptor<Component> message = ArgumentCaptor.forClass(Component.class);
		verify(player).sendMessage(message.capture());

		return ((TextComponent) message.getValue()).content();
	}
}
