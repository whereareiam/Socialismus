package me.whereareiam.socialismus.common.chat.render;

import com.google.inject.Provider;
import me.whereareiam.socialismus.model.chat.ChatMessages;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.model.chat.render.ChatRenderContext;
import me.whereareiam.socialismus.model.config.Commands;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.registry.base.Registry;
import me.whereareiam.socialismus.service.chat.render.ChatPlaceholderResolver;
import me.whereareiam.socialismus.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClearPlaceholderResolverTest {
	@Mock
	private Provider<ChatSettings> chatSettings;
	@Mock
	private Provider<ChatMessages> chatMessages;
	@Mock
	private Provider<Commands> commands;
	@Mock
	private Registry<ChatPlaceholderResolver> registry;

	@Test
	void resolveReturnsEmptyWhenRecipientMissing() {
		ClearPlaceholderResolver resolver = new ClearPlaceholderResolver(
				chatSettings,
				chatMessages,
				commands,
				registry
		);

		FormattedChatMessage message = FormattedChatMessage.builder().id(1).build();
		Component resolved = resolver.resolve(new ChatRenderContext(message, null));

		assertEquals("", ComponentUtil.toPlain(resolved));
	}

	@Test
	void resolveReturnsEmptyWhenPermissionMissing() {
		ChatSettings settings = new ChatSettings();
		ChatSettings.ChatHistorySettings history = new ChatSettings.ChatHistorySettings();
		history.setPermission("chat.clear");
		settings.setHistory(history);

		ChatMessages messages = new ChatMessages();
		ChatMessages.ClearFormat clearFormat = new ChatMessages.ClearFormat();
		clearFormat.setFormat("<gray>[clear]</gray>");
		messages.setClearFormat(clearFormat);

		when(chatSettings.get()).thenReturn(settings);
		when(chatMessages.get()).thenReturn(messages);

		SocialismusPlayer recipient = mock(SocialismusPlayer.class,
				withSettings().useConstructor(UUID.randomUUID(), "recipient").defaultAnswer(CALLS_REAL_METHODS));
		when(recipient.hasPermission("chat.clear")).thenReturn(false);

		ClearPlaceholderResolver resolver = new ClearPlaceholderResolver(
				chatSettings,
				chatMessages,
				commands,
				registry
		);

		FormattedChatMessage message = FormattedChatMessage.builder().id(2).build();
		Component resolved = resolver.resolve(new ChatRenderContext(message, recipient));

		assertEquals("", ComponentUtil.toPlain(resolved));
	}
}
