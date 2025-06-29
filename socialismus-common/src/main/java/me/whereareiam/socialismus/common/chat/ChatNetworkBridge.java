package me.whereareiam.socialismus.common.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.input.chat.ChatSyncBus;
import me.whereareiam.socialismus.api.model.chat.ChatSyncPacket;
import me.whereareiam.socialismus.api.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.api.output.SerializationService;
import me.whereareiam.socialismus.api.output.resource.sync.SyncService;

import java.util.Set;

@Singleton
public class ChatNetworkBridge implements ChatSyncBus {
	private static final String CHANNEL = "socialismus:chat";

	private final SyncService sync;
	private final SerializationService serializationService;
	private final ChatCoordinator coordinator;
	private final String serverId;

	@Inject
	public ChatNetworkBridge(
			SyncService sync,
			SerializationService serializationService,
			ChatCoordinator coordinator
	) {
		this.sync = sync;
		this.serializationService = serializationService;
		this.coordinator = coordinator;
		this.serverId = System.getProperty("socialismus.server",
				"default-server");
	}

	@Override
	public void publish(ChatMessage message) {
		try {
			ChatSyncPacket packet = new ChatSyncPacket(serverId, message);
			byte[] data = serializationService.serialize(packet);
			sync.publish(CHANNEL, data);
		} catch (Exception ex) {
			Logger.warn("Failed to sync chat message: " + ex);
		}
	}

	@Override
	public void startListening() {
		sync.subscribe(CHANNEL, (channel, payload) -> {
			try {
				ChatSyncPacket packet = serializationService.deserialize(payload, ChatSyncPacket.class);

				if (serverId.equals(packet.getOrigin())) return;

				packet.getMessage().setRecipients(Set.of());

				coordinator.coordinate(packet.getMessage());
			} catch (Exception ex) {
				Logger.warn("Bad chat-sync packet: " + ex);
			}
		});
	}
}
