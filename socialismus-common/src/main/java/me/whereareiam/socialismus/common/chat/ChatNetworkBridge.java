package me.whereareiam.socialismus.common.chat;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.api.Logger;
import me.whereareiam.socialismus.api.input.chat.ChatSyncBus;
import me.whereareiam.socialismus.api.model.chat.ChatSyncPacket;
import me.whereareiam.socialismus.api.model.chat.message.ChatMessage;
import me.whereareiam.socialismus.api.output.PlatformInteractor;
import me.whereareiam.socialismus.api.output.SerializationService;
import me.whereareiam.socialismus.api.output.resource.sync.SyncService;
import me.whereareiam.socialismus.api.util.ComponentUtil;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

@Singleton
public class ChatNetworkBridge implements ChatSyncBus {
	private static final String CHANNEL = "socialismus:chat";

	private final SyncService sync;
	private final SerializationService serializationService;
	private final ChatCoordinator coordinator;
	private final PlatformInteractor platformInteractor;
	private final String serverId;

	@Inject
	public ChatNetworkBridge(
			SyncService sync,
			SerializationService serializationService,
			ChatCoordinator coordinator,
			PlatformInteractor platformInteractor,
			PlatformInteractor platformInteractor1
	) {
		this.sync = sync;
		this.serializationService = serializationService;
		this.coordinator = coordinator;
		this.platformInteractor = platformInteractor1;

		String serverIdentifier = platformInteractor.getServerIp() + ":" + platformInteractor.getServerPort();
		this.serverId = UUID.nameUUIDFromBytes(serverIdentifier.getBytes(StandardCharsets.UTF_8)).toString();
	}

	@Override
	public void publish(ChatMessage message) {
		try {
			String content = ComponentUtil.toString(message.getContent());
			ChatSyncPacket packet = new ChatSyncPacket(serverId, content, message);

			byte[] data = serializationService.serialize(packet);
			sync.publish(CHANNEL, data);

			Logger.debug("Published chat message to sync channel: " + message.getId());
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

				ChatMessage message = packet.getMessage();
				message.setContent(ComponentUtil.toMiniMessage(packet.getContent()));
				message.setRecipients(Set.of());
				message.getSender().setInteractor(platformInteractor);

				Logger.debug("Received chat message from sync channel: " + message.getId());
				coordinator.coordinate(message);
			} catch (Exception ex) {
				Logger.warn("Bad chat-sync packet: " + ex);
				ex.printStackTrace();
			}
		});
	}
}
