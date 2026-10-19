package me.whereareiam.socialismus.module.channelizer.platform.bukkit;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.channelizer.api.PlatformMessageBus;
import org.jetbrains.annotations.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Singleton
public final class BukkitMessageBus implements PlatformMessageBus, PluginMessageListener, Listener {
	private static final String PM_CHANNEL = "BungeeCord";
	/**
	 * What a plugin message from a server to its proxy carries at most, less what the forwarding header takes.
	 */
	private static final int MAX_PAYLOAD = 32_000;
	/**
	 * How long after the first player joins messages are sent through them; right at the join the connection is not
	 * ready for plugin messages yet.
	 */
	private static final long CARRIER_DELAY_TICKS = 20;

	private final Plugin plugin;
	private final Map<String, List<Consumer<byte[]>>> listeners = new ConcurrentHashMap<>();
	private final List<Runnable> sendableListeners = new CopyOnWriteArrayList<>();

	@Inject
	public BukkitMessageBus(Plugin plugin) {
		this.plugin = plugin;
		plugin.getServer().getMessenger()
				.registerOutgoingPluginChannel(plugin, PM_CHANNEL);
		plugin.getServer().getMessenger()
				.registerIncomingPluginChannel(plugin, PM_CHANNEL, this);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public int maxPayloadSize() {
		return MAX_PAYLOAD;
	}

	@Override
	public boolean canSend() {
		return !Bukkit.getOnlinePlayers().isEmpty();
	}

	@Override
	public void onSendable(@NotNull Runnable listener) {
		sendableListeners.add(listener);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (Bukkit.getOnlinePlayers().size() != 1) return;

		Bukkit.getScheduler().runTaskLater(plugin, () -> sendableListeners.forEach(Runnable::run), CARRIER_DELAY_TICKS);
	}

	@Override
	public void publish(@NotNull String channel, byte @NotNull [] payload) {
		Logger.debug("Publishing to channel: " + channel + ", payload length: " + payload.length);
		Player carrier = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
		if (carrier == null) {
			Logger.debug("No player online to carry the message");
			return;
		}

		if (payload.length > MAX_PAYLOAD) {
			Logger.warn("Not sending %d bytes on %s: a plugin message carries at most %d", payload.length, channel, MAX_PAYLOAD);
			return;
		}

		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Forward");
		out.writeUTF("ALL");
		out.writeUTF(channel);
		out.writeShort(payload.length);
		out.write(payload);

		Logger.debug("Sending plugin message to channel: " + channel);
		carrier.sendPluginMessage(plugin, PM_CHANNEL, out.toByteArray());
	}

	@Override
	public void subscribe(@NotNull String channel, @NotNull Consumer<byte[]> receiver) {
		Logger.debug("Subscribing to channel: " + channel);
		listeners.computeIfAbsent(channel, k -> new CopyOnWriteArrayList<>()).add(receiver);
	}

	@Override
	public void onPluginMessageReceived(String transportChannel, Player player, byte[] message) {
		Logger.debug("Received plugin message from channel: " + transportChannel);
		if (!PM_CHANNEL.equals(transportChannel)) return;

		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String logicalChannel = in.readUTF();
		Logger.debug("Received message on logical channel: " + logicalChannel);

		int len = in.readUnsignedShort();
		byte[] payload = new byte[len];
		in.readFully(payload);

		listeners.getOrDefault(logicalChannel, List.of()).forEach(receiver -> receiver.accept(payload));
	}

}
