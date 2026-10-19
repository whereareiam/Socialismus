package me.whereareiam.socialismus.module.link.common.transport;

import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.assertTrue;

final class LinkTesting {
	private LinkTesting() {
	}

	static void awaitConnected(LinkClient client) throws InterruptedException {
		await(client::isConnected, "The client did not connect to the hub");
	}

	static void await(BooleanSupplier condition, String failure) throws InterruptedException {
		long deadline = System.nanoTime() + 10_000_000_000L;
		while (!condition.getAsBoolean() && System.nanoTime() < deadline) Thread.sleep(20);

		assertTrue(condition.getAsBoolean(), failure);
	}
}
