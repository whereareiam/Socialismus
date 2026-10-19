package me.whereareiam.socialismus.module.channelizer.network;

import me.whereareiam.socialismus.module.channelizer.api.PlatformMessageBus;
import me.whereareiam.socialismus.service.resource.sync.SyncSubscriber;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChannelizerReplicationTransportTest {
	private final PlatformMessageBus bus = mock(PlatformMessageBus.class);
	private final ChannelizerReplicationTransport transport = new ChannelizerReplicationTransport(bus);

	@Test
	void sendsAndReceivesTheNetworksMessagesAsPluginMessages() {
		List<byte[]> received = new ArrayList<>();
		transport.subscribe("socialismus:network", received::add);
		ArgumentCaptor<SyncSubscriber> subscriber = ArgumentCaptor.forClass(SyncSubscriber.class);
		verify(bus).subscribe(eq("socialismus:network"), subscriber.capture());

		transport.publish("socialismus:network", new byte[]{1});
		subscriber.getValue().onMessage("socialismus:network", new byte[]{2});

		verify(bus).publish("socialismus:network", new byte[]{1});
		assertArrayEquals(new byte[]{2}, received.getFirst());
	}

	@Test
	void carriesAsMuchAsAPluginMessageAndOnlyWhileOneCanBeSent() {
		when(bus.maxPayloadSize()).thenReturn(32000);
		when(bus.canSend()).thenReturn(true);

		assertEquals(32000, transport.maxMessageSize());
		assertTrue(transport.isConnected());
	}

	@Test
	void reportsAReconnectOnceAPluginMessageCanBeSentAgain() {
		Runnable listener = mock(Runnable.class);

		transport.onReconnect(listener);

		verify(bus).onSendable(listener);
	}
}
