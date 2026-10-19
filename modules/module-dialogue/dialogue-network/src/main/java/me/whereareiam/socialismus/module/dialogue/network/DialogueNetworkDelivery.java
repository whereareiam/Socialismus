package me.whereareiam.socialismus.module.dialogue.network;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.model.scheduler.DelayedRunnableTask;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.type.DeliveryStatus;
import me.whereareiam.socialismus.module.dialogue.common.DialogueNotices;
import me.whereareiam.socialismus.module.dialogue.common.delivery.LocalDelivery;
import me.whereareiam.socialismus.module.dialogue.common.delivery.MessageRenderer;
import me.whereareiam.socialismus.module.dialogue.common.delivery.RemoteDelivery;
import me.whereareiam.socialismus.module.network.api.NetworkService;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import me.whereareiam.socialismus.module.network.api.model.Origin;
import me.whereareiam.socialismus.module.network.api.player.NetworkPlayer;
import me.whereareiam.socialismus.module.network.api.replication.ReplicatedChannel;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import me.whereareiam.socialismus.service.Scheduler;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Delivers private messages through SocialismusNetwork: a message goes to the node that hosts its recipient, which
 * delivers it like one written there and answers whether it reached them. A message the node does not answer within
 * {@code messaging.confirmation} tells the sender the recipient is not online.
 */
public final class DialogueNetworkDelivery implements RemoteDelivery {
	static final String MESSAGES = "dialogue:messages";
	static final String CONFIRMATIONS = "dialogue:confirmations";

	private final NetworkService network;
	private final ReplicatedChannel<MessageRecord> messages;
	private final ReplicatedChannel<ConfirmationRecord> confirmations;
	private final LocalDelivery localDelivery;
	private final MessageRenderer renderer;
	private final DialogueNotices notices;
	private final PlayerRegistry playerRegistry;
	private final Scheduler scheduler;
	private final Supplier<Duration> confirmation;
	private final Map<String, Message> awaiting = new ConcurrentHashMap<>();

	public DialogueNetworkDelivery(
			@NotNull NetworkService network,
			@NotNull LocalDelivery localDelivery,
			@NotNull MessageRenderer renderer,
			@NotNull DialogueNotices notices,
			@NotNull PlayerRegistry playerRegistry,
			@NotNull Scheduler scheduler,
			@NotNull Supplier<Duration> confirmation
	) {
		this.network = network;
		this.messages = network.replication().channel(MESSAGES, MessageRecord.class);
		this.confirmations = network.replication().channel(CONFIRMATIONS, ConfirmationRecord.class);
		this.localDelivery = localDelivery;
		this.renderer = renderer;
		this.notices = notices;
		this.playerRegistry = playerRegistry;
		this.scheduler = scheduler;
		this.confirmation = confirmation;

		messages.subscribe(this::receive);
		confirmations.subscribe(this::confirm);
	}

	@Override
	public boolean mayReach(@NotNull String playerName) {
		return network.players().find(playerName).isPresent();
	}

	@Override
	public void send(@NotNull Message message) {
		Optional<NetworkPlayer> recipient = network.players().find(message.getRecipientName());
		if (recipient.isEmpty()) {
			undelivered(message, DeliveryStatus.RECIPIENT_OFFLINE, message.getRecipientName());
			return;
		}

		SocialismusPlayer sender = message.getSender();
		awaiting.put(message.getId(), message);
		messages.publish(new MessageRecord(
				message.getId(),
				recipient.get().getNode().value(),
				sender.getUniqueId().toString(),
				sender.getUsername(),
				sender.getServer(),
				message.getRecipientName(),
				message.getContent(),
				message.getTimestamp() == null ? System.currentTimeMillis() : message.getTimestamp().toEpochMilli(),
				message.getConversationId(),
				message.isBypassesToggle(),
				message.isExemptFromIgnore(),
				message.isHiddenFromSpies()
		));

		scheduler.schedule(DelayedRunnableTask.builder()
				.module("dialogue")
				.delay(confirmation.get().toMillis())
				.runnable(() -> expire(message))
				.build());
	}

	private void receive(MessageRecord record, Origin origin) {
		if (!isMine(record.getTarget()) || origin.getNode() == null) return;

		UUID senderId = UUID.fromString(record.getSender());
		SocialismusPlayer sender = network.players().find(senderId)
				.map(SocialismusPlayer.class::cast)
				.orElseGet(() -> new NetworkPlayer(senderId, record.getSenderName(), origin.getNode(), record.getSenderServer()));
		Message message = Message.builder()
				.id(record.getId())
				.sender(sender)
				.recipientName(record.getRecipientName())
				.content(record.getContent())
				.timestamp(Instant.ofEpochMilli(record.getTimestamp()))
				.deliveryStatus(DeliveryStatus.PENDING_DELIVERY)
				.conversationId(record.getConversationId())
				.bypassesToggle(record.isBypassesToggle())
				.exemptFromIgnore(record.isExemptFromIgnore())
				.hiddenFromSpies(record.isHiddenFromSpies())
				.build();

		DeliveryStatus status = localDelivery.deliver(message, false);
		String recipientName = playerRegistry.getPlayerData(record.getRecipientName())
				.map(SocialismusPlayer::getUsername)
				.orElse(record.getRecipientName());
		confirmations.publish(new ConfirmationRecord(
				record.getId(),
				origin.getNode().value(),
				recipientName,
				status == DeliveryStatus.DELIVERED_LOCALLY ? DeliveryStatus.DELIVERED_REMOTELY : status
		));
	}

	private void confirm(ConfirmationRecord record, Origin origin) {
		if (!isMine(record.getTarget())) return;

		Message message = awaiting.remove(record.getId());
		if (message == null) return;

		if (record.getStatus() == DeliveryStatus.DELIVERED_REMOTELY) {
			message.setDeliveryStatus(DeliveryStatus.DELIVERED_REMOTELY);
			message.setRecipientName(record.getRecipientName());
			renderer.showSent(message, null);
			return;
		}

		undelivered(message, record.getStatus(), record.getRecipientName());
	}

	private void expire(Message message) {
		if (awaiting.remove(message.getId()) == null) return;

		Logger.debug("No node confirmed the private message %s in time", message.getId());
		undelivered(message, DeliveryStatus.RECIPIENT_OFFLINE, message.getRecipientName());
	}

	private void undelivered(Message message, DeliveryStatus status, String recipientName) {
		message.setDeliveryStatus(status);
		notices.undelivered(message.getSender(), recipientName, status);
	}

	private boolean isMine(String target) {
		return target != null && network.self().getId().equals(NodeId.of(target));
	}
}
