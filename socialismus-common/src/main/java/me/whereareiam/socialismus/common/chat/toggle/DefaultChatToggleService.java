package me.whereareiam.socialismus.common.chat.toggle;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.keystone.model.SerializerContent;
import me.whereareiam.socialismus.Serializer;
import me.whereareiam.socialismus.model.chat.ChatMessages;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.InternalChat;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.service.chat.ChatToggleService;
import me.whereareiam.socialismus.service.chat.ChatToggleStore;
import me.whereareiam.socialismus.service.container.ChatContainerService;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class DefaultChatToggleService implements ChatToggleService {
	private final Provider<ChatSettings> settings;
	private final Provider<ChatMessages> messages;
	private final ChatContainerService chats;
	private final ChatToggleStore toggles;

	@Override
	public void toggle(@NotNull SocialismusPlayer player, @NotNull String chatId) {
		ChatMessages.Toggle notices = messages.get().getToggle();
		if (!settings.get().getToggle().isEnabled()) {
			tell(player, notices.getUnavailable(), chatId);
			return;
		}

		Optional<InternalChat> chat = chats.getChat(chatId);
		if (chat.isEmpty()) {
			tell(player, notices.getUnknown(), chatId);
			return;
		}
		if (!chat.get().isToggleable()) {
			tell(player, notices.getNotToggleable(), chatId);
			return;
		}

		boolean off = !toggles.isOff(player.getUniqueId(), chatId);
		toggles.setOff(player.getUniqueId(), chatId, off);
		tell(player, off ? notices.getOff() : notices.getOn(), chatId);
	}

	private static void tell(SocialismusPlayer player, String notice, String chatId) {
		player.sendMessage(Serializer.serialize(SerializerContent.builder()
				.receiver(player)
				.message(notice)
				.placeholder("chat", chatId)
				.build()));
	}
}
