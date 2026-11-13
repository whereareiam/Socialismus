package me.whereareiam.socialismus.module.dialogue.common;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSettings;
import me.whereareiam.socialismus.module.dialogue.api.model.conversation.Conversation;
import me.whereareiam.socialismus.module.dialogue.api.type.DeliveryStatus;
import me.whereareiam.socialismus.module.dialogue.common.config.defaults.DialogueSettingsDefaults;
import me.whereareiam.socialismus.service.style.StyleService;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test data builder utility for creating test objects
 */
public class TestDataBuilder {
	public static Conversation.ConversationBuilder<?, ?> conversationBetween(String player1, String player2) {
		return Conversation.builder()
				.id(UUID.randomUUID().toString())
				.participants(Set.of(player1, player2))
				.messages(new ArrayList<>())
				.messageCount(0)
				.lastActivity(Instant.now());
	}

	public static Message.MessageBuilder<?, ?> messageFrom(SocialismusPlayer sender, String recipientName) {
		return Message.builder()
				.id(UUID.randomUUID().toString())
				.sender(sender)
				.recipientName(recipientName)
				.timestamp(Instant.now())
				.deliveryStatus(DeliveryStatus.PENDING_DELIVERY)
				.content("Test message");
	}

	public static SocialismusPlayer mockPlayer(String username) {
		SocialismusPlayer player = mock(SocialismusPlayer.class);
		when(player.getUsername()).thenReturn(username);
		when(player.getUniqueId()).thenReturn(UUID.nameUUIDFromBytes(username.getBytes(StandardCharsets.UTF_8)));
		when(player.canSee(any())).thenReturn(true);
		return player;
	}

	/**
	 * Styles that read every text as plain text, as Socialismus does before its styles are bound.
	 */
	public static StyleService plainStyles() {
		return new StyleService() {
			@Override
			public @NotNull Optional<String> styleOf(@NotNull SocialismusPlayer writer, @NotNull List<String> styles) {
				return Optional.empty();
			}

			@Override
			public @NotNull Component read(@NotNull String text, String style) {
				return Component.text(text);
			}
		};
	}

	public static DialogueSettings defaultSettings() {
		return new DialogueSettingsDefaults().supply(new DialogueSettings());
	}
}
