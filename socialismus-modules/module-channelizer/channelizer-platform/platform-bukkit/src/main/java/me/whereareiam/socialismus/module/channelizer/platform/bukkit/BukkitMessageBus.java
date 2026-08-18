package me.whereareiam.socialismus.module.channelizer.platform.bukkit;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.module.channelizer.api.PlatformMessageBus;
import me.whereareiam.socialismus.service.resource.sync.SyncSubscriber;
import org.jetbrains.annotations.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public final class BukkitMessageBus implements PlatformMessageBus, PluginMessageListener {
	private static final String PM_CHANNEL = "BungeeCord";

	private final Plugin plugin;
	private final Map<String, List<SyncSubscriber>> listeners = new HashMap<>();

	@Inject
	public BukkitMessageBus(Plugin plugin) {
		this.plugin = plugin;
		plugin.getServer().getMessenger()
				.registerOutgoingPluginChannel(plugin, PM_CHANNEL);
		plugin.getServer().getMessenger()
				.registerIncomingPluginChannel(plugin, PM_CHANNEL, this);
	}

	@Override
	public void publish(@NotNull String channel, byte @NotNull [] payload) {
		Logger.debug("Publishing to channel: " + channel + ", payload length: " + payload.length);
		Player carrier = Bukkit.getOnlinePlayers().stream().findFirst().orElse(null);
		if (carrier == null) {
			Logger.debug("No player online to carry the message");
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
	public void subscribe(@NotNull String channel, @NotNull SyncSubscriber subscriber) {
		Logger.debug("Subscribing to channel: " + channel);
		listeners.computeIfAbsent(channel, k -> new ArrayList<>()).add(subscriber);
	}

	@Override
	public void onPluginMessageReceived(String transportChannel, Player player, byte[] message) {
		Logger.debug("Received plugin message from channel: " + transportChannel);
		if (!PM_CHANNEL.equals(transportChannel)) return;

		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String logicalChannel = in.readUTF();
		Logger.debug("Received message on logical channel: " + logicalChannel);

		int len = in.readShort();
		byte[] payload = new byte[len];
		in.readFully(payload);

		listeners.getOrDefault(logicalChannel, List.of())
				.forEach(sub -> {
					Logger.debug("Dispatching message to subscriber: " + sub.getClass().getName());
					sub.onMessage(logicalChannel, payload);
				});
	}

}
