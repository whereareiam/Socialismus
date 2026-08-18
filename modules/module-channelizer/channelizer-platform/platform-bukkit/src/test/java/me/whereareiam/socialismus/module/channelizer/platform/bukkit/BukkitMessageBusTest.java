package me.whereareiam.socialismus.module.channelizer.platform.bukkit;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.service.resource.sync.SyncSubscriber;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class BukkitMessageBusTest {
	@BeforeEach
	void setUp() {
		Logger.init(new LoggingHelper() {
			@Override
			public void info(String message, Object... objects) {
			}

			@Override
			public void warn(String message, Object... objects) {
			}

			@Override
			public void severe(String message, Object... objects) {
			}

			@Override
			public void debug(String message, Object... objects) {
			}
		});
	}

	@Test
	void constructorRegistersPluginChannels() {
		Plugin plugin = mock(Plugin.class);
		Server server = mock(Server.class);
		Messenger messenger = mock(Messenger.class);
		when(plugin.getServer()).thenReturn(server);
		when(server.getMessenger()).thenReturn(messenger);

		BukkitMessageBus bus = new BukkitMessageBus(plugin);

		verify(messenger).registerOutgoingPluginChannel(plugin, "BungeeCord");
		verify(messenger).registerIncomingPluginChannel(plugin, "BungeeCord", bus);
	}

	@Test
	void onPluginMessageReceivedDispatchesMatchingSubscriber() throws IOException {
		Plugin plugin = mock(Plugin.class);
		Server server = mock(Server.class);
		Messenger messenger = mock(Messenger.class);
		Player player = mock(Player.class);
		SyncSubscriber subscriber = mock(SyncSubscriber.class);
		when(plugin.getServer()).thenReturn(server);
		when(server.getMessenger()).thenReturn(messenger);

		BukkitMessageBus bus = new BukkitMessageBus(plugin);
		bus.subscribe("sync", subscriber);

		byte[] payload = new byte[] {10, 20, 30};
		byte[] message = forwardedMessage("sync", payload);

		bus.onPluginMessageReceived("BungeeCord", player, message);

		verify(subscriber).onMessage("sync", payload);
	}

	@Test
	void onPluginMessageReceivedIgnoresDifferentTransportChannel() throws IOException {
		Plugin plugin = mock(Plugin.class);
		Server server = mock(Server.class);
		Messenger messenger = mock(Messenger.class);
		Player player = mock(Player.class);
		SyncSubscriber subscriber = mock(SyncSubscriber.class);
		when(plugin.getServer()).thenReturn(server);
		when(server.getMessenger()).thenReturn(messenger);

		BukkitMessageBus bus = new BukkitMessageBus(plugin);
		bus.subscribe("sync", subscriber);

		bus.onPluginMessageReceived("custom:channel", player, forwardedMessage("sync", new byte[] {1}));

		verifyNoInteractions(subscriber);
	}

	private static byte[] forwardedMessage(String logicalChannel, byte[] payload) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(output);
		data.writeUTF(logicalChannel);
		data.writeShort(payload.length);
		data.write(payload);
		return output.toByteArray();
	}
}
