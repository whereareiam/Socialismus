package me.whereareiam.socialismus.module.network.testing;

import me.whereareiam.socialismus.module.network.api.transport.ReplicationTransport;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What every {@link ReplicationTransport} must do for SocialismusNetwork. A transport module's test extends it and
 * hands out connected transports of one network.
 */
public abstract class ReplicationTransportContract {
	private static final Duration TIMEOUT = Duration.ofSeconds(10);

	/**
	 * Returns a new transport of the network under test, connected and ready to subscribe.
	 */
	protected abstract ReplicationTransport transport(String node) throws Exception;

	@Test
	void deliversAMessageToAnotherNode() throws Exception {
		ReplicationTransport sender = transport("a");
		ReplicationTransport receiver = transport("b");
		List<String> received = subscribe(receiver, "contract:one");

		send(sender, "contract:one", "hello");

		awaitSize(received, 1);
		assertEquals(List.of("hello"), received);
	}

	@Test
	void keepsTheOrderOfOneSender() throws Exception {
		ReplicationTransport sender = transport("a");
		ReplicationTransport receiver = transport("b");
		List<String> received = subscribe(receiver, "contract:order");

		for (int i = 0; i < 100; i++) send(sender, "contract:order", Integer.toString(i));

		awaitSize(received, 100);
		for (int i = 0; i < 100; i++) assertEquals(Integer.toString(i), received.get(i));
	}

	@Test
	void keepsChannelsApart() throws Exception {
		ReplicationTransport sender = transport("a");
		ReplicationTransport receiver = transport("b");
		List<String> wanted = subscribe(receiver, "contract:wanted");
		List<String> other = subscribe(receiver, "contract:other");

		send(sender, "contract:other", "elsewhere");
		send(sender, "contract:wanted", "here");

		awaitSize(wanted, 1);
		awaitSize(other, 1);
		assertEquals(List.of("here"), wanted);
		assertEquals(List.of("elsewhere"), other);
	}

	@Test
	void reportsThatItIsConnected() throws Exception {
		assertTrue(transport("a").isConnected());
	}

	private static List<String> subscribe(ReplicationTransport transport, String channel) throws InterruptedException {
		List<String> received = new CopyOnWriteArrayList<>();
		transport.subscribe(channel, message -> received.add(new String(message, StandardCharsets.UTF_8)));
		// A subscription takes effect asynchronously on some transports.
		Thread.sleep(200);

		return received;
	}

	private static void send(ReplicationTransport transport, String channel, String message) {
		transport.publish(channel, message.getBytes(StandardCharsets.UTF_8));
	}

	private static void awaitSize(List<String> received, int size) throws InterruptedException {
		long deadline = System.nanoTime() + TIMEOUT.toNanos();
		while (received.size() < size && System.nanoTime() < deadline) Thread.sleep(20);

		assertEquals(size, received.size(), "Messages received: " + received);
	}
}
