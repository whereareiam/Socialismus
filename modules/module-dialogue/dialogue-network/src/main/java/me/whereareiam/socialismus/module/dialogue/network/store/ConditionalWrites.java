package me.whereareiam.socialismus.module.dialogue.network.store;

import me.whereareiam.socialismus.module.network.api.replication.ReplicatedCache;
import me.whereareiam.socialismus.module.network.api.replication.Versioned;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Changes an entry of a replicated cache from what it holds when the change is written: read, change, write only
 * while unchanged, and again when another node changed it in between. So two nodes changing one entry at the same
 * moment both take effect.
 */
final class ConditionalWrites {
	private static final int ATTEMPTS = 10;
	private static final long TIMEOUT_SECONDS = 5;

	private ConditionalWrites() {
	}

	/**
	 * Changes an entry.
	 *
	 * @param cache  the cache
	 * @param key    the entry's key
	 * @param change turns the current value, or {@code null} for none, into the new one, or {@code null} to remove it
	 * @param ttl    how long the new value lives, or {@code null} until changed
	 * @return the value after the change, or {@code null} when it was removed
	 * @throws IllegalStateException when the entry kept changing or the write did not answer
	 */
	static <T> @Nullable T change(
			@NotNull ReplicatedCache<T> cache,
			@NotNull String key,
			@NotNull Function<@Nullable T, @Nullable T> change,
			@Nullable Duration ttl
	) {
		for (int attempt = 0; attempt < ATTEMPTS; attempt++) {
			Optional<Versioned<T>> read = cache.read(key);
			T changed = change.apply(read.map(Versioned::getValue).orElse(null));
			if (changed == null) {
				if (read.isEmpty() || await(cache.remove(key, read.get()))) return null;
				continue;
			}

			if (await(cache.write(key, read.orElse(null), changed, ttl))) return changed;
		}

		throw new IllegalStateException("The entry " + key + " kept changing");
	}

	private static boolean await(CompletableFuture<Boolean> written) {
		try {
			return written.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
		} catch (Exception failure) {
			throw new IllegalStateException("The network did not answer a write", failure);
		}
	}
}
