package me.whereareiam.socialismus.common.store;

import me.whereareiam.socialismus.common.config.SerializationServiceAdapter;
import me.whereareiam.socialismus.logging.Logger;
import me.whereareiam.socialismus.logging.LoggingHelper;
import me.whereareiam.socialismus.store.Store;
import me.whereareiam.socialismus.store.StoreKey;
import me.whereareiam.socialismus.store.StorePersistence;
import me.whereareiam.socialismus.store.StoreReplication;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class DefaultStoresTest {
	private static final UUID ALICE = UUID.fromString("00000000-0000-0000-0000-00000000a11c");
	private static final StoreKey<UUID, Mode> MODES = StoreKey.of("mentionator:modes", UUID.class, Mode.class).replicated().persistent();
	private static final StoreKey<String, Integer> COUNTS = StoreKey.of("mentionator:counts", String.class, Integer.class);

	private final SteppedClock clock = new SteppedClock();
	private final DefaultStores stores = new DefaultStores(new SerializationServiceAdapter(), clock);

	@BeforeEach
	void silenceTheLog() {
		Logger.init(mock(LoggingHelper.class));
	}

	@Test
	void keepsValuesInMemoryWhileNothingIsOffered_ForgettingThemWhenTheyExpire() {
		Store<String, Integer> counts = stores.of(COUNTS);

		counts.put("alice", 1);
		counts.put("bob", 2, Duration.ofSeconds(10));
		clock.advance(Duration.ofSeconds(10));

		assertEquals(Optional.of(1), counts.get("alice"));
		assertEquals(Optional.empty(), counts.get("bob"));
		assertEquals(Map.of("alice", 1), counts.entries());
	}

	@Test
	void oneNameIsOneStore_AndAnotherKeyForItIsRefused() {
		assertEquals(stores.of(COUNTS), stores.of(COUNTS));
		assertThrows(IllegalArgumentException.class, () -> stores.of(COUNTS.replicated()));
		assertThrows(IllegalArgumentException.class, () -> StoreKey.of("Modes", String.class, String.class));
	}

	@Test
	void aReplicatedStoreMovesItsValuesToTheReplicationAndBack_HearingOnlyTheCopyInUse() {
		Store<UUID, Mode> modes = stores.of(MODES);
		List<String> heard = new ArrayList<>();
		modes.onChange((key, previous, current) -> heard.add(key + "=" + current));
		modes.put(ALICE, Mode.DIRECT);
		SharedReplication replication = new SharedReplication(clock);

		stores.addReplication(replication);

		assertEquals(Optional.of(Mode.DIRECT), replication.shared.get(ALICE.toString()));
		replication.shared.put(ALICE.toString(), Mode.NONE, null);
		assertEquals(Optional.of(Mode.NONE), modes.get(ALICE));

		stores.removeReplication(replication);
		replication.shared.put(ALICE.toString(), Mode.ALL, null);

		assertEquals(Optional.of(Mode.NONE), modes.get(ALICE));
		assertEquals(List.of(ALICE + "=DIRECT", ALICE + "=NONE"), heard);
	}

	@Test
	void aPersistentStoreLoadsWhatItLacks_AndSavesAndDeletesItsChanges() {
		Store<UUID, Mode> modes = stores.of(MODES);
		UUID bob = UUID.randomUUID();
		modes.put(ALICE, Mode.DIRECT);
		SavedPersistence persistence = new SavedPersistence();
		persistence.rows.put(ALICE.toString(), new StorePersistence.Saved("\"NONE\"", null));
		persistence.rows.put(bob.toString(), new StorePersistence.Saved("\"NONE\"", null));

		stores.addPersistence(persistence);

		assertEquals(Optional.of(Mode.DIRECT), modes.get(ALICE));
		assertEquals(Optional.of(Mode.NONE), modes.get(bob));

		modes.put(ALICE, Mode.ALL);
		modes.remove(bob);

		assertEquals(Map.of(ALICE.toString(), new StorePersistence.Saved("\"ALL\"", null)), persistence.rows);
	}

	@Test
	void aStoreThatIsNotPersistentIgnoresPersistence() {
		Store<String, Integer> counts = stores.of(COUNTS);
		SavedPersistence persistence = new SavedPersistence();
		stores.addPersistence(persistence);

		counts.put("alice", 1);

		assertEquals(Map.of(), persistence.rows);
	}

	private enum Mode {
		ALL,
		DIRECT,
		NONE
	}

	// One copy every "server" shares, as a replication would offer it.
	private static final class SharedReplication implements StoreReplication {
		private final MemoryEntries<Mode> shared;

		private SharedReplication(Clock clock) {
			this.shared = new MemoryEntries<>(clock);
		}

		@Override
		@SuppressWarnings("unchecked")
		public <V> @NotNull Entries<V> share(@NotNull String name, @NotNull Class<V> valueType) {
			return (Entries<V>) shared;
		}
	}

	// The rows of one store, saved and deleted at once.
	private static final class SavedPersistence implements StorePersistence {
		private final Map<String, Saved> rows = new ConcurrentHashMap<>();

		@Override
		public @NotNull CompletableFuture<Map<String, Saved>> load(@NotNull String store) {
			return CompletableFuture.completedFuture(Map.copyOf(rows));
		}

		@Override
		public @NotNull CompletableFuture<Void> save(@NotNull String store, @NotNull String key, @NotNull Saved saved) {
			rows.put(key, saved);
			return CompletableFuture.completedFuture(null);
		}

		@Override
		public @NotNull CompletableFuture<Void> delete(@NotNull String store, @NotNull String key) {
			rows.remove(key);
			return CompletableFuture.completedFuture(null);
		}
	}

	private static final class SteppedClock extends Clock {
		private Instant now = Instant.parse("2026-11-13T12:00:00Z");

		void advance(Duration duration) {
			now = now.plus(duration);
		}

		@Override
		public ZoneId getZone() {
			return ZoneOffset.UTC;
		}

		@Override
		public Clock withZone(ZoneId zone) {
			return this;
		}

		@Override
		public Instant instant() {
			return now;
		}
	}
}
