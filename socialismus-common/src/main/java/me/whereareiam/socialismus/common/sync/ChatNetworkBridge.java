package me.whereareiam.socialismus.common.sync;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.Logger;
import me.whereareiam.socialismus.common.chat.ChatCoordinator;
import me.whereareiam.socialismus.input.sync.ChatSyncBus;
import me.whereareiam.socialismus.model.chat.ChatSettings;
import me.whereareiam.socialismus.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.model.chat.message.FormattedChatMessage;
import me.whereareiam.socialismus.output.SerializationService;
import me.whereareiam.socialismus.output.resource.sync.SyncService;
import net.kyori.adventure.text.Component;

import java.util.Set;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class ChatNetworkBridge implements ChatSyncBus {
	private static final String CHANNEL = Constants.Channels.CHAT;

	private final SyncService sync;
	private final SerializationService serializationService;
	private final ChatCoordinator coordinator;

	private final Provider<ChatSettings> chatSettings;

	private final Component emptyComponent = Component.empty();

	public void initialize() {
		if (!chatSettings.get().getSynchronization().isEnabled())
			return;

		subscribe();
	}

	@Override
	public void publish(FormattedChatMessage message) {
		if (!chatSettings.get().getSynchronization().isEnabled())
			return;

		try {
			message.setOrigin(Constants.Synchronization.IDENTIFIER);
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
			FormattedChatMessage message = serializationService.deserialize(payload, FormattedChatMessage.class);

			if (Constants.Synchronization.IDENTIFIER.equals(message.getOrigin())) return;

			message.setRecipients(Set.of());

			if (message.getFormat() != null
					&& !message.getFormat().equals(emptyComponent)
					&& chatSettings.get().getSynchronization().isPreserveFormat()) {
				coordinator.coordinate(message);
				return;
			}

			coordinator.coordinate((ChatMessage) message);
		} catch (Exception ex) {
			Logger.warn("Bad chat-sync packet: " + ex);
		}
	}
}
