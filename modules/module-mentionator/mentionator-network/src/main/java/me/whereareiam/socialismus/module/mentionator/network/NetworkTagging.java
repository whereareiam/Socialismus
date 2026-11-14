package me.whereareiam.socialismus.module.mentionator.network;

import me.whereareiam.socialismus.model.player.SocialismusPlayer;
import me.whereareiam.socialismus.module.mentionator.common.notify.Notifier;
import me.whereareiam.socialismus.module.mentionator.common.notify.RemoteTagging;
import me.whereareiam.socialismus.module.mentionator.common.notify.SoundCue;
import me.whereareiam.socialismus.module.mentionator.common.notify.TagDelivery;
import me.whereareiam.socialismus.module.network.api.NetworkService;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import me.whereareiam.socialismus.module.network.api.model.Origin;
import me.whereareiam.socialismus.module.network.api.player.NetworkPlayer;
import me.whereareiam.socialismus.module.network.api.replication.ReplicatedChannel;
import me.whereareiam.socialismus.registry.PlayerRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Tags players on other nodes through SocialismusNetwork: every player of the network may be tagged, and the node
 * that hosts a tagged player notifies them, checking their mode and the limits there. Records go to every node on
 * {@code mentionator:tags}, and the node they name takes them; nothing waits for an answer.
 */
public final class NetworkTagging implements RemoteTagging {
	static final String CHANNEL = "mentionator:tags";

	private final NetworkService network;
	private final ReplicatedChannel<TagRecord> channel;
	private final Notifier notifier;
	private final PlayerRegistry players;
	private volatile boolean closed;

	public NetworkTagging(@NotNull NetworkService network, @NotNull Notifier notifier, @NotNull PlayerRegistry players) {
		this.network = network;
		this.channel = network.replication().channel(CHANNEL, TagRecord.class);
		this.notifier = notifier;
		this.players = players;

		channel.subscribe(this::receive);
	}

	@Override
	public @NotNull Collection<? extends SocialismusPlayer> players() {
		return network.players().players();
	}

	@Override
	public void send(@NotNull UUID target, @NotNull TagDelivery delivery) {
		network.players().find(target).ifPresent(player -> channel.publish(new TagRecord(
				player.getNode().value(),
				target.toString(),
				delivery,
				List.of()
		)));
	}

	@Override
	public void playSounds(@NotNull UUID player, @NotNull List<SoundCue> sounds) {
		network.players().find(player).ifPresent(hosted -> channel.publish(new TagRecord(
				hosted.getNode().value(),
				player.toString(),
				null,
				sounds
		)));
	}

	/**
	 * Stops taking records, once Mentionator leaves the network.
	 */
	public void close() {
		closed = true;
	}

	private void receive(TagRecord record, Origin origin) {
		if (closed || !isMine(record.getTarget()) || record.getPlayer() == null) return;

		UUID playerId = UUID.fromString(record.getPlayer());
		if (record.getDelivery() == null) {
			notifier.playSounds(playerId, record.getSounds() == null ? List.of() : record.getSounds());
			return;
		}

		Optional<SocialismusPlayer> player = players.getPlayerData(playerId);
		if (player.isEmpty()) return;

		TagDelivery delivery = record.getDelivery();
		notifier.deliver(player.get(), author(delivery, origin), delivery);
	}

	// The player who tagged, as this node knows them: hosted here, announced by their node, or as the record names them.
	private SocialismusPlayer author(TagDelivery delivery, Origin origin) {
		return players.getPlayerData(delivery.sender())
				.or(() -> network.players().find(delivery.sender()).map(SocialismusPlayer.class::cast))
				.orElseGet(() -> new NetworkPlayer(delivery.sender(), delivery.senderName(), origin.getNode(), delivery.senderServer()));
	}

	private boolean isMine(String target) {
		return target != null && network.self().getId().equals(NodeId.of(target));
	}
}
