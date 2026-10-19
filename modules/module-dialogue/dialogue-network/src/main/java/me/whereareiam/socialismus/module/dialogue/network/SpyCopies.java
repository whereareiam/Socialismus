package me.whereareiam.socialismus.module.dialogue.network;

import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.base.EventOrder;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.dialogue.api.event.DialogueDeliveredEvent;
import me.whereareiam.socialismus.module.dialogue.api.model.Message;
import me.whereareiam.socialismus.module.dialogue.api.type.DeliveryStatus;
import me.whereareiam.socialismus.module.dialogue.common.moderation.Spying;
import me.whereareiam.socialismus.module.network.api.NetworkService;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import me.whereareiam.socialismus.module.network.api.model.Origin;
import me.whereareiam.socialismus.module.network.api.player.NetworkPlayer;
import me.whereareiam.socialismus.module.network.api.replication.ReplicatedChannel;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.UUID;

/**
 * Lets the spies on every node see a private message delivered on one of them: the node that delivered it shows it to
 * its own spies and sends a copy to the others, which show it to theirs.
 */
public final class SpyCopies implements EventListener {
	static final String CHANNEL = "dialogue:spy-copies";

	private final NetworkService network;
	private final ReplicatedChannel<SpyRecord> copies;
	private final Spying spying;

	public SpyCopies(@NotNull NetworkService network, @NotNull Spying spying) {
		this.network = network;
		this.copies = network.replication().channel(CHANNEL, SpyRecord.class);
		this.spying = spying;

		copies.subscribe(this::receive);
	}

	@SocialisticEvent(EventOrder.HIGHEST)
	public void onDelivered(@NotNull DialogueDeliveredEvent event) {
		Message message = event.getMessage();
		SocialismusPlayer recipient = event.getRecipient();
		if (!spying.isSpiedOn(message, recipient)) return;

		SocialismusPlayer sender = message.getSender();
		copies.publish(new SpyRecord(
				message.getId(),
				sender.getUniqueId().toString(),
				sender.getUsername(),
				sender.getServer(),
				recipient.getUniqueId().toString(),
				recipient.getUsername(),
				recipient.getServer(),
				message.getContent(),
				message.getTimestamp() == null ? System.currentTimeMillis() : message.getTimestamp().toEpochMilli()
		));
	}

	private void receive(SpyRecord record, Origin origin) {
		if (origin.getNode() == null) return;

		SocialismusPlayer sender = player(record.getSender(), record.getSenderName(), record.getSenderServer(), origin.getNode());
		SocialismusPlayer recipient = player(record.getRecipient(), record.getRecipientName(), record.getRecipientServer(), origin.getNode());
		Message message = Message.builder()
				.id(record.getId())
				.sender(sender)
				.recipientName(record.getRecipientName())
				.content(record.getContent())
				.timestamp(Instant.ofEpochMilli(record.getTimestamp()))
				.deliveryStatus(DeliveryStatus.DELIVERED_REMOTELY)
				.build();

		spying.showToSpies(message, recipient);
	}

	private SocialismusPlayer player(String uniqueId, String name, String server, NodeId origin) {
		UUID id = UUID.fromString(uniqueId);

		return network.players().find(id)
				.map(SocialismusPlayer.class::cast)
				.orElseGet(() -> new NetworkPlayer(id, name, origin, server));
	}
}
