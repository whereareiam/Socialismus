package me.whereareiam.socialismus.module.channelizer.network;

import me.whereareiam.socialismus.module.channelizer.api.PlatformMessageBus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
		@SuppressWarnings("unchecked")
		ArgumentCaptor<Consumer<byte[]>> receiver = ArgumentCaptor.forClass(Consumer.class);
		verify(bus).subscribe(eq("socialismus:network"), receiver.capture());

		transport.publish("socialismus:network", new byte[]{1});
		receiver.getValue().accept(new byte[]{2});

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
