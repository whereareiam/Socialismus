package me.whereareiam.socialismus.module.database.common.store;

import me.whereareiam.dialectica.adapter.jdbi.entity.EntityAccess;
import me.whereareiam.socialismus.store.StorePersistence;
import org.jetbrains.annotations.NotNull;

import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static me.whereareiam.socialismus.module.database.common.store.StoreEntryEntityColumns.EXPIRES_AT;
import static me.whereareiam.socialismus.module.database.common.store.StoreEntryEntityColumns.STORE_NAME;

/**
 * Keeps Socialismus's persistent stores in the table of {@link StoreSchema}, one row per value. Every load, save and
 * deletion runs on a thread of its own, in the order asked, so no server thread waits for the database; loading a
 * store forgets its rows that expired.
 */
public final class DatabaseStorePersistence implements StorePersistence, AutoCloseable {
	private final EntityAccess<StoreEntryEntity> rows;
	private final Clock clock;
	private final ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
		Thread thread = new Thread(runnable, "SocialismusDatabase stores");
		thread.setDaemon(true);
		return thread;
	});

	/**
	 * Keeps the stores in a database whose {@link StoreSchema} is installed.
	 *
	 * @param rows  the access to the table's rows
	 * @param clock the clock expiry is judged by
	 */
	public DatabaseStorePersistence(@NotNull EntityAccess<StoreEntryEntity> rows, @NotNull Clock clock) {
		this.rows = rows;
		this.clock = clock;
	}

	@Override
	public @NotNull CompletableFuture<Map<String, Saved>> load(@NotNull String store) {
		return CompletableFuture.supplyAsync(() -> loaded(store, clock.instant()), executor);
	}

	@Override
	public @NotNull CompletableFuture<Void> save(@NotNull String store, @NotNull String key, @NotNull Saved saved) {
		StoreEntryEntity row = new StoreEntryEntity(store, key, saved.value(), saved.expiresAt());

		return CompletableFuture.runAsync(() -> rows.upsert(row), executor);
	}

	@Override
	public @NotNull CompletableFuture<Void> delete(@NotNull String store, @NotNull String key) {
		return CompletableFuture.runAsync(() -> rows.delete(store, key), executor);
	}

	/**
	 * Finishes what was asked before, then stops the thread.
	 */
	@Override
	public void close() throws InterruptedException {
		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.SECONDS);
	}

	private Map<String, Saved> loaded(String store, Instant now) {
		rows.delete(STORE_NAME.is(store).and(EXPIRES_AT.atMost(now)));

		Map<String, Saved> values = new LinkedHashMap<>();
		for (StoreEntryEntity row : rows.query(STORE_NAME.is(store)).list())
			values.put(row.entryKey(), new Saved(row.entryValue(), row.expiresAt()));

		return values;
	}
}
