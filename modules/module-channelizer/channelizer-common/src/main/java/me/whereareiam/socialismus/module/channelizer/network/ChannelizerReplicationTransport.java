package me.whereareiam.socialismus.module.channelizer.network;

import me.whereareiam.socialismus.module.channelizer.api.PlatformMessageBus;
import me.whereareiam.socialismus.module.network.api.transport.ReplicationTransport;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Carries the network's messages as plugin messages, forwarded by the proxy to every other server. They travel only
 * while players are online on the sending and the receiving server, so the network catches up whenever an empty
 * server gets its first player again.
 */
public final class ChannelizerReplicationTransport implements ReplicationTransport {
	private final PlatformMessageBus bus;

	public ChannelizerReplicationTransport(@NotNull PlatformMessageBus bus) {
		this.bus = bus;
	}

	@Override
	public @NotNull String name() {
		return "plugin messages";
	}

	@Override
	public void publish(@NotNull String channel, byte @NotNull [] message) {
		bus.publish(channel, message);
	}

	@Override
	public void subscribe(@NotNull String channel, @NotNull Consumer<byte[]> receiver) {
		bus.subscribe(channel, receiver);
	}

	@Override
	public int maxMessageSize() {
		return bus.maxPayloadSize();
	}

	@Override
	public boolean isConnected() {
		return bus.canSend();
	}

	@Override
	public void onReconnect(@NotNull Runnable listener) {
		bus.onSendable(listener);
	}
}
