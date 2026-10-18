package me.whereareiam.socialismus.module.dialogue.common.delivery;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.Constants;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.module.dialogue.api.model.DeliveryConfirmation;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.model.config.DialogueSync;
import me.whereareiam.socialismus.module.dialogue.api.type.DeliveryStatus;
import me.whereareiam.socialismus.module.dialogue.common.DialogueNotices;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.service.Scheduler;
import me.whereareiam.socialismus.service.SerializationService;
import me.whereareiam.socialismus.service.resource.sync.SyncService;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Carries private messages to the other servers of the network and their delivery confirmations back. A message
 * the recipient's server does not confirm within {@code confirmation} of {@code sync} tells the sender the recipient
 * is not online.
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public final class DialogueNetworkBridge {
	private static final String CHANNEL = Constants.Channels.NAME + ":dialogue";
	private static final String DELIVERY_CHANNEL = Constants.Channels.NAME + ":dialogue_delivery";

	private final SyncService sync;
	private final Scheduler scheduler;
	private final SerializationService serializer;
	private final PlayerRegistry playerRegistry;
	private final Provider<DialogueSync> settings;
	private final LocalDelivery localDelivery;
	private final MessageRenderer renderer;
	private final DialogueNotices notices;

	private final Map<String, Message> awaitingConfirmation = new ConcurrentHashMap<>();

	/**
	 * Returns whether private messages go to the other servers: Socialismus syncs and {@code sync} is enabled.
	 */
	public boolean isEnabled() {
		return Constants.Synchronization.SYNCHRONIZATION && settings.get().isEnabled();
	}

	/**
	 * Starts receiving the messages and confirmations of the other servers, when enabled.
	 */
	public void initialize() {
		if (!isEnabled()) return;

		sync.subscribe(CHANNEL, (channel, data) -> receive(data));
		sync.subscribe(DELIVERY_CHANNEL, (channel, data) -> confirm(data));
	}

	/**
	 * Sends a message to the other servers and waits for one of them to confirm it delivered it.
	 *
	 * @param message the message, whose recipient is not on this server
	 */
	public void publish(@NotNull Message message) {
		try {
			awaitingConfirmation.put(message.getId(), message);
			sync.publish(CHANNEL, serializer.serialize(message));
			Logger.debug("Synced PM from " + message.getSender().getUsername());
		} catch (Exception ex) {
			Logger.warn("Failed to sync PM: " + ex);
		}

		scheduler.schedule(DelayedRunnableTask.builder()
				.module("dialogue")
				.delay(settings.get().getConfirmation().toMillis())
				.runnable(() -> expire(message))
				.build());
	}

	private void expire(Message message) {
		if (awaitingConfirmation.remove(message.getId()) == null) return;

		message.setDeliveryStatus(DeliveryStatus.RECIPIENT_OFFLINE);
		notices.noRecipient(message.getSender(), message.getRecipientName());
	}

	private void receive(byte[] data) {
		try {
			Message message = serializer.deserialize(data, Message.class);
			if (Constants.Synchronization.IDENTIFIER.equals(message.getOrigin())) return;
			if (!localDelivery.deliver(message, false)) return;

			sendConfirmation(message);
		} catch (Exception ex) {
			Logger.warn("Bad PM-sync packet: " + ex);
		}
	}

	private void sendConfirmation(Message message) {
		String recipientName = playerRegistry.getPlayerData(message.getRecipientName())
				.map(recipient -> recipient.getUsername())
				.orElse(message.getRecipientName());

		DeliveryConfirmation confirmation = DeliveryConfirmation.builder()
				.id(message.getId())
				.senderName(message.getSender().getUsername())
				.recipientName(recipientName)
				.status(DeliveryStatus.DELIVERED_REMOTELY)
				.originServer(message.getOrigin())
				.deliveryServer(Constants.Synchronization.IDENTIFIER)
				.deliveryTime(Instant.now())
				.build();

		sync.publish(DELIVERY_CHANNEL, serializer.serialize(confirmation));
		Logger.debug("Sent delivery confirmation for PM to " + message.getRecipientName());
	}

	private void confirm(byte[] data) {
		try {
			DeliveryConfirmation confirmation = serializer.deserialize(data, DeliveryConfirmation.class);
			if (!Constants.Synchronization.IDENTIFIER.equals(confirmation.getOriginServer())) return;

			Message message = awaitingConfirmation.remove(confirmation.getId());
			if (message == null) return;

			message.setDeliveryStatus(DeliveryStatus.DELIVERED_REMOTELY);
			message.setRecipientName(confirmation.getRecipientName());
			renderer.showSent(message, null);
		} catch (Exception ex) {
			Logger.warn("Bad delivery confirmation packet: " + ex);
		}
	}
}
