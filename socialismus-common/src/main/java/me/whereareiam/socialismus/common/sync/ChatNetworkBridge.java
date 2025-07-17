package me.whereareiam.socialismus.common.sync;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.api.Constants;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.input.sync.ChatSyncBus;
import me.whereareiam.socialismus.api.model.chat.ChatSettings;
import me.whereareiam.socialismus.api.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.api.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.api.output.PlatformInteractor;
import me.whereareiam.socialismus.api.output.SerializationService;
import me.whereareiam.socialismus.api.output.resource.sync.SyncService;
import me.whereareiam.socialismus.common.chat.ChatCoordinator;
import me.whereareiam.socialismus.common.chat.broadcast.ChatBroadcaster;
import me.whereareiam.socialismus.api.input.container.ChatHistoryContainerService;

import java.util.Set;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ChatNetworkBridge implements ChatSyncBus {
	private static final String CHANNEL = Constants.Channels.CHAT;

        private final SyncService sync;
        private final SerializationService serializationService;
        private final ChatCoordinator coordinator;
        private final PlatformInteractor platformInteractor;
        private final ChatBroadcaster broadcaster;
        private final ChatHistoryContainerService historyContainer;

        private final Provider<ChatSettings> chatSettings;

	public void initialize() {
		if (!chatSettings.get().getSynchronization().isEnabled())
			return;

		subscribe();
	}

	@Override
	public void publish(ChatMessage message) {
		if (!chatSettings.get().getSynchronization().isEnabled())
			return;

		try {
			message.setOrigin(Constants.IDENTIFIER);
			byte[] data = serializationService.serialize(message);
			sync.publish(CHANNEL, data);

			Logger.debug("Published chat message to sync channel: " + message.getId());
		} catch (Exception ex) {
			Logger.warn("Failed to sync chat message: " + ex);
		}
	}

	@Override
	public void subscribe() {
		if (!chatSettings.get().getSynchronization().isEnabled())
			return;

		sync.subscribe(CHANNEL, (channel, payload) -> handleEvent(payload));
	}

	private void handleEvent(byte[] payload) {
                try {
                        if (chatSettings.get().getSynchronization().isPreFormatMessages()) {
                                FormattedChatMessage formatted = serializationService.deserialize(payload, FormattedChatMessage.class);

                                if (Constants.IDENTIFIER.equals(formatted.getOrigin())) return;

                                formatted.setRecipients(Set.of());
                                formatted.getSender().setInteractor(platformInteractor);

                                Logger.debug("Received formatted chat message from sync channel: " + formatted.getId());
                                broadcaster.broadcast(formatted);
                                historyContainer.addMessage(formatted.getId(), formatted);
                                return;
                        }

                        ChatMessage chatMessage = serializationService.deserialize(payload, ChatMessage.class);

                        if (Constants.IDENTIFIER.equals(chatMessage.getOrigin())) return;

                        chatMessage.setRecipients(Set.of());
                        chatMessage.getSender().setInteractor(platformInteractor);

                        Logger.debug("Received chat message from sync channel: " + chatMessage.getId());
                        coordinator.coordinate(chatMessage);
                } catch (Exception ex) {
                        Logger.warn("Bad chat-sync packet: " + ex);
                }
	}
}
