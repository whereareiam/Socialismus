package me.whereareiam.socialismus.module.network.common.replication;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.network.api.model.NodeId;
import me.whereareiam.socialismus.module.network.api.model.Origin;
import me.whereareiam.socialismus.module.network.api.replication.ReplicatedChannel;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

/**
 * A replicated channel: what one node publishes, every other node's subscribers receive.
 *
 * @param <T> message type
 */
final class DefaultReplicatedChannel<T> implements ReplicatedChannel<T> {
	private final String name;
	private final Class<T> type;
	private final ReplicationContext context;
	private final List<BiConsumer<T, Origin>> receivers = new CopyOnWriteArrayList<>();

	DefaultReplicatedChannel(String name, Class<T> type, ReplicationContext context) {
		this.name = name;
		this.type = type;
		this.context = context;
	}

	Class<T> type() {
		return type;
	}

	@Override
	public void publish(@NotNull T message) {
		context.send(new Envelope(context.self(), Envelope.Kind.MESSAGE, name, context.serialization().serialize(message)));
	}

	@Override
	public void subscribe(@NotNull BiConsumer<T, Origin> receiver) {
		receivers.add(receiver);
	}

	/**
	 * Hands a message another node published to the subscribers.
	 */
	void receive(byte @NotNull [] body, @NotNull NodeId sender) {
		T message;
		try {
			message = context.serialization().deserialize(body, type);
		} catch (RuntimeException failure) {
			Logger.warn("Ignoring a message of %s from %s that cannot be read: %s", name, sender, failure.getMessage());
			return;
		}

		Origin origin = Origin.remote(sender);
		for (BiConsumer<T, Origin> receiver : receivers) {
			try {
				receiver.accept(message, origin);
			} catch (RuntimeException failure) {
				Logger.warn("A subscriber of %s failed: %s", name, failure);
			}
		}
	}
}
