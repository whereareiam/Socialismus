package me.whereareiam.socialismus.module.network.testing;

import me.whereareiam.socialismus.module.network.api.transport.ReplicationStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What every {@link ReplicationStore} must do for SocialismusNetwork. A store's test extends it; each test works in
 * a namespace of its own.
 */
public abstract class ReplicationStoreContract {
	private String namespace;

	/**
	 * Returns the store under test.
	 */
	protected abstract ReplicationStore store() throws Exception;

	/**
	 * Lets the given time pass for the store's expiry: waits, or moves a test clock.
	 */
	protected abstract void pass(Duration duration) throws Exception;

	@BeforeEach
	void useOwnNamespace() {
		namespace = "contract-" + UUID.randomUUID();
	}

	@Test
	void storesAnEntryOnlyWhereNoneIsExpected() throws Exception {
		ReplicationStore store = store();

		assertTrue(store.compareAndPut(namespace, "key", null, bytes("one"), null));
		assertFalse(store.compareAndPut(namespace, "key", null, bytes("two"), null));
		assertArrayEquals(bytes("one"), store.get(namespace, "key").orElseThrow());
	}

	@Test
	void replacesAnEntryOnlyWhileItIsAsExpected() throws Exception {
		ReplicationStore store = store();
		store.compareAndPut(namespace, "key", null, bytes("one"), null);

		assertFalse(store.compareAndPut(namespace, "key", bytes("other"), bytes("two"), null));
		assertTrue(store.compareAndPut(namespace, "key", bytes("one"), bytes("two"), null));
		assertArrayEquals(bytes("two"), store.get(namespace, "key").orElseThrow());
	}

	@Test
	void removesAnEntryOnlyWhileItIsAsExpected() throws Exception {
		ReplicationStore store = store();
		store.compareAndPut(namespace, "key", null, bytes("one"), null);

		assertFalse(store.compareAndRemove(namespace, "key", bytes("other")));
		assertTrue(store.compareAndRemove(namespace, "key", bytes("one")));
		assertTrue(store.get(namespace, "key").isEmpty());
	}

	@Test
	void listsTheEntriesOfANamespace() throws Exception {
		ReplicationStore store = store();
		store.compareAndPut(namespace, "a", null, bytes("1"), null);
		store.compareAndPut(namespace, "b", null, bytes("2"), null);
		store.compareAndPut(namespace + "-other", "c", null, bytes("3"), null);

		Map<String, byte[]> entries = store.entries(namespace);

		assertEquals(2, entries.size());
		assertArrayEquals(bytes("1"), entries.get("a"));
		assertArrayEquals(bytes("2"), entries.get("b"));
	}

	@Test
	void forgetsAnEntryThatExpired() throws Exception {
		ReplicationStore store = store();
		store.compareAndPut(namespace, "key", null, bytes("one"), Duration.ofSeconds(1));

		pass(Duration.ofMillis(1500));

		assertTrue(store.get(namespace, "key").isEmpty());
		assertTrue(store.entries(namespace).isEmpty());
		assertTrue(store.compareAndPut(namespace, "key", null, bytes("two"), null));
	}

	private static byte[] bytes(String text) {
		return text.getBytes(StandardCharsets.UTF_8);
	}
}
