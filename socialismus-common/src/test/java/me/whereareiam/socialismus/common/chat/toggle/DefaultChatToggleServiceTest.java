package me.whereareiam.socialismus.common.chat.toggle;

import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.keystone.serializer.SerializerEngine;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.common.config.defaults.chat.ChatMessagesDefaults;
import me.whereareiam.socialismus.common.config.defaults.chat.ChatSettingsDefaults;
import me.whereareiam.socialismus.model.chat.Chat;
import me.whereareiam.socialismus.model.chat.ChatMessages;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.InternalChat;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.service.container.ChatContainerService;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DefaultChatToggleServiceTest {
	private final ChatSettings settings = new ChatSettingsDefaults().supply(new ChatSettings());
	private final ChatMessages messages = new ChatMessagesDefaults().supply(new ChatMessages());
	private final ChatContainerService chats = mock(ChatContainerService.class);
	private final InMemoryChatToggleStore toggles = new InMemoryChatToggleStore();
	private final DefaultChatToggleService service = new DefaultChatToggleService(() -> settings, () -> messages, chats, toggles);
	private final SocialismusPlayer bob = mock(SocialismusPlayer.class);
	private final UUID bobId = UUID.randomUUID();

	@BeforeEach
	void setUp() {
		SerializerEngine engine = mock(SerializerEngine.class);
		when(engine.serialize(any(SerializerContent.class)))
				.thenAnswer(call -> Component.text(call.getArgument(0, SerializerContent.class).getMessage()));
		Serializer.initialize(() -> engine);
		when(bob.getUniqueId()).thenReturn(bobId);
		when(chats.getChat(any())).thenReturn(Optional.empty());
		when(chats.getChat("global")).thenReturn(Optional.of(chat("global", true)));
		when(chats.getChat("staff")).thenReturn(Optional.of(chat("staff", false)));
	}

	@Test
	void turnsAChatOffAndBackOn() {
		service.toggle(bob, "global");
		assertTrue(toggles.isOff(bobId, "global"));
		assertEquals(messages.getToggle().getOff(), told());

		service.toggle(bob, "global");
		assertFalse(toggles.isOff(bobId, "global"));
	}

	@Test
	void refusesAChatThatCannotBeTurnedOff() {
		service.toggle(bob, "staff");

		assertFalse(toggles.isOff(bobId, "staff"));
		assertEquals(messages.getToggle().getNotToggleable(), told());
	}

	@Test
	void refusesAnUnknownChat() {
		service.toggle(bob, "nope");

		assertEquals(messages.getToggle().getUnknown(), told());
	}

	@Test
	void refusesWhenChatsCannotBeTurnedOff() {
		settings.getToggle().setEnabled(false);

		service.toggle(bob, "global");

		assertFalse(toggles.isOff(bobId, "global"));
		assertEquals(messages.getToggle().getUnavailable(), told());
	}

	private String told() {
		ArgumentCaptor<Component> notice = ArgumentCaptor.forClass(Component.class);
		verify(bob, atLeastOnce()).sendMessage(notice.capture());

		return ((net.kyori.adventure.text.TextComponent) notice.getValue()).content();
	}

	private static InternalChat chat(String id, boolean toggleable) {
		return InternalChat.from(Chat.builder().id(id).enabled(true).triggers(List.of()).formats(List.of())
				.requirements(Map.of()).toggleable(toggleable).build());
	}
}
