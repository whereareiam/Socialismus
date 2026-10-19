package me.whereareiam.socialismus.module.link.common.transport;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class LinkHubTest {
	private static final String SECRET = "a shared secret";

	@BeforeAll
	static void initLogger() {
		Logger.init(mock(LoggingHelper.class));
	}

	@Test
	void theProxyAndItsServersReachEachOther() throws Exception {
		try (LinkHub hub = new LinkHub(new InetSocketAddress("127.0.0.1", 0), SECRET);
		     LinkClient server = new LinkClient(hub.address(), SECRET)) {
			LinkTesting.awaitConnected(server);
			List<String> atHub = new CopyOnWriteArrayList<>();
			List<String> atServer = new CopyOnWriteArrayList<>();
			hub.subscribe("chat", message -> atHub.add(text(message)));
			server.subscribe("chat", message -> atServer.add(text(message)));

			server.publish("chat", bytes("from the server"));
			hub.publish("chat", bytes("from the proxy"));

			LinkTesting.await(() -> atHub.size() == 1 && atServer.size() == 1, "A message did not arrive");
			assertEquals(List.of("from the server"), atHub);
			assertEquals(List.of("from the proxy"), atServer);
		}
	}

	@Test
	void aServerWithAnotherSecretIsRefused() throws Exception {
		try (LinkHub hub = new LinkHub(new InetSocketAddress("127.0.0.1", 0), SECRET);
		     LinkClient stranger = new LinkClient(hub.address(), "a guess")) {
			Thread.sleep(500);

			assertFalse(stranger.isConnected());
			assertEquals(0, hub.connected());
		}
	}

	@Test
	void aServerConnectsAgainAfterTheProxyRestartedAndReportsIt() throws Exception {
		LinkHub hub = new LinkHub(new InetSocketAddress("127.0.0.1", 0), SECRET);
		InetSocketAddress address = hub.address();
		try (LinkClient server = new LinkClient(address, SECRET)) {
			LinkTesting.awaitConnected(server);
			CountDownLatch reconnected = new CountDownLatch(1);
			server.onReconnect(reconnected::countDown);

			hub.close();
			LinkTesting.await(() -> !server.isConnected(), "The server did not notice the proxy stopping");
			try (LinkHub restarted = new LinkHub(address, SECRET)) {
				assertTrue(reconnected.await(15, TimeUnit.SECONDS), "The server did not report that it reconnected");
				assertTrue(server.isConnected());
				assertEquals(1, restarted.connected());
			}
		}
	}

	@Test
	void aLargeMessageArrivesWhole() throws Exception {
		try (LinkHub hub = new LinkHub(new InetSocketAddress("127.0.0.1", 0), SECRET);
		     LinkClient sender = new LinkClient(hub.address(), SECRET);
		     LinkClient receiver = new LinkClient(hub.address(), SECRET)) {
			LinkTesting.awaitConnected(sender);
			LinkTesting.awaitConnected(receiver);
			List<byte[]> received = new CopyOnWriteArrayList<>();
			receiver.subscribe("snapshot", received::add);
			byte[] large = new byte[2_000_000];
			large[large.length - 1] = 42;

			sender.publish("snapshot", large);

			LinkTesting.await(() -> received.size() == 1, "The large message did not arrive");
			assertEquals(large.length, received.getFirst().length);
			assertEquals(42, received.getFirst()[large.length - 1]);
		}
	}

	private static byte[] bytes(String text) {
		return text.getBytes(StandardCharsets.UTF_8);
	}

	private static String text(byte[] bytes) {
		return new String(bytes, StandardCharsets.UTF_8);
	}
}
