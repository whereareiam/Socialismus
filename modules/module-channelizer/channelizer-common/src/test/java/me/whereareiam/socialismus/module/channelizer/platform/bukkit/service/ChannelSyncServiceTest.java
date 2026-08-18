package me.whereareiam.socialismus.module.channelizer.platform.bukkit.service;

import me.whereareiam.socialismus.module.channelizer.api.PlatformMessageBus;
import me.whereareiam.socialismus.service.resource.sync.SyncSubscriber;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ChannelSyncServiceTest {
	@Test
	void publishDelegatesToPlatformBus() {
		PlatformMessageBus bus = mock(PlatformMessageBus.class);
		ChannelSyncService service = new ChannelSyncService(bus);
		byte[] payload = new byte[] {1, 2, 3};

		service.publish("sync", payload);

		verify(bus).publish("sync", payload);
	}

	@Test
	void subscribeDelegatesToPlatformBus() {
		PlatformMessageBus bus = mock(PlatformMessageBus.class);
		ChannelSyncService service = new ChannelSyncService(bus);
		SyncSubscriber subscriber = mock(SyncSubscriber.class);

		service.subscribe("sync", subscriber);

		verify(bus).subscribe("sync", subscriber);
	}
}
