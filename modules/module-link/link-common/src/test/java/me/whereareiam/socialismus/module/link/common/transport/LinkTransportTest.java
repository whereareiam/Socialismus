package me.whereareiam.socialismus.module.link.common.transport;

import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.module.network.api.transport.ReplicationTransport;
import me.whereareiam.socialismus.module.network.testing.ReplicationTransportContract;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

/**
 * Servers on one link: every transport is a server's client, and the proxy's hub relays between them.
 */
class LinkTransportTest extends ReplicationTransportContract {
	private static final String SECRET = "a shared secret";

	private final List<AutoCloseable> opened = new ArrayList<>();
	private LinkHub hub;

	@BeforeAll
	static void initLogger() {
		Logger.init(mock(LoggingHelper.class));
	}

	@BeforeEach
	void startHub() throws IOException {
		hub = new LinkHub(new InetSocketAddress("127.0.0.1", 0), SECRET);
		opened.add(hub);
	}

	@AfterEach
	void close() throws Exception {
		for (AutoCloseable closeable : opened) closeable.close();
	}

	@Override
	protected ReplicationTransport transport(String node) throws InterruptedException {
		LinkClient client = new LinkClient(hub.address(), SECRET);
		opened.add(client);
		LinkTesting.awaitConnected(client);

		return client;
	}
}
