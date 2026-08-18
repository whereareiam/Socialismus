package me.whereareiam.socialismus.module.essentials.feature.dialogue.sync;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.service.SerializationService;
import me.whereareiam.socialismus.service.resource.sync.SyncService;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.config.DialogueSettings;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.model.DeliveryConfirmation;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.model.Dialogue;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.service.dialogue.type.LocalDialogueService;
import me.whereareiam.socialismus.module.essentials.feature.dialogue.type.DeliveryStatus;

import java.time.Instant;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class DialogueNetworkBridge {
	private static final String CHANNEL = Constants.Channels.NAME + ":dialogue";
	private static final String DELIVERY_CHANNEL = Constants.Channels.NAME + ":dialogue_delivery";

	private final SyncService sync;
	private final Provider<DialogueSettings> settings;
	private final SerializationService serializer;
	private final LocalDialogueService dialogueService;

	public void initialize() {
		subscribe();
		subscribeToDeliveryConfirmations();
	}

	public void publish(Dialogue pm) {
		DialogueSettings.Synchronization syncSettings = settings.get().getSynchronization();
		if (!syncSettings.isEnabled()) return;

		try {
			pm.setOrigin(Constants.Synchronization.IDENTIFIER);
			sync.publish(CHANNEL, serializer.serialize(pm));
			Logger.debug("Synced PM from " + pm.getSender().getUsername());
		} catch (Exception ex) {
			Logger.warn("Failed to sync PM: " + ex);
		}
	}

	public void subscribe() {
		DialogueSettings.Synchronization syncSettings = settings.get().getSynchronization();
		if (!syncSettings.isEnabled()) return;

		this.sync.subscribe(CHANNEL, (c, data) -> {
			try {
				Dialogue pm = serializer.deserialize(data, Dialogue.class);
				if (Constants.Synchronization.IDENTIFIER.equals(pm.getOrigin())) return;

				boolean delivered = dialogueService.deliverLocally(pm);

				// Send delivery confirmation back to origin server
				if (delivered) {
					sendDeliveryConfirmation(pm);
				}
			} catch (Exception ex) {
				Logger.warn("Bad PM-sync packet: " + ex);
			}
		});
	}

	private void subscribeToDeliveryConfirmations() {
		DialogueSettings.Synchronization syncSettings = settings.get().getSynchronization();
		if (!syncSettings.isEnabled()) return;

		sync.subscribe(DELIVERY_CHANNEL, (c, data) -> {
			try {
				DeliveryConfirmation confirmation = serializer.deserialize(data, DeliveryConfirmation.class);
				if (Constants.Synchronization.IDENTIFIER.equals(confirmation.getOriginServer())) return;

				// Update local message status to DELIVERED_REMOTELY
				Logger.debug("Message delivered remotely: " + confirmation.getSenderName() + " -> " + confirmation.getRecipientName());
				Logger.debug("Received delivery confirmation for message: " + confirmation.getId());
			} catch (Exception ex) {
				Logger.warn("Bad delivery confirmation packet: " + ex);
			}
		});
	}

	private void sendDeliveryConfirmation(Dialogue pm) {
		try {
			DeliveryConfirmation confirmation = DeliveryConfirmation.builder()
					.id(pm.getSender().getUsername() + ":" + UUID.randomUUID())
					.senderName(pm.getSender().getUsername())
					.recipientName(pm.getRecipientName())
					.status(DeliveryStatus.DELIVERED_REMOTELY)
					.originServer(pm.getOrigin())
					.deliveryServer(Constants.Synchronization.IDENTIFIER)
					.deliveryTime(Instant.now())
					.build();

			sync.publish(DELIVERY_CHANNEL, serializer.serialize(confirmation));
			Logger.debug("Sent delivery confirmation for PM to " + pm.getRecipientName());
		} catch (Exception ex) {
			Logger.warn("Failed to send delivery confirmation: " + ex);
		}
	}
}
