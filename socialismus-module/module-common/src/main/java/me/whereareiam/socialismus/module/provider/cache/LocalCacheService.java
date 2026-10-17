package me.whereareiam.socialismus.module.provider.cache;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.model.scheduler.PeriodicalRunnableTask;
import me.whereareiam.socialismus.service.Scheduler;
import me.whereareiam.socialismus.service.resource.CacheService;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory cache implementation with TTL support.
 * Used when Redis is not available (standalone mode).
 */
@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public final class LocalCacheService implements CacheService {
	private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
	private final Map<String, Set<String>> sets = new ConcurrentHashMap<>();
	private final Scheduler scheduler;

	private static final long CLEANUP_INTERVAL_MS = 60_000; // 1 minute

	@Inject
	public void initialize() {
		// Schedule periodic cleanup task
		PeriodicalRunnableTask cleanupTask = PeriodicalRunnableTask.builder()
				.module("socialismus")
				.runnable(this::cleanup)
				.delay(CLEANUP_INTERVAL_MS)
				.period(CLEANUP_INTERVAL_MS)
				.build();

		scheduler.schedule(cleanupTask, true);
	}

	@Override
	public <T> Optional<T> get(String key, Class<T> type) {
		CacheEntry entry = cache.get(key);
		if (entry == null || entry.isExpired()) {
			cache.remove(key);
			return Optional.empty();
		}

		try {
			return Optional.of(type.cast(entry.value));
		} catch (ClassCastException e) {
			return Optional.empty();
		}
	}

	@Override
	public <T> void put(String key, T value) {
		cache.put(key, new CacheEntry(value, null));
	}

	@Override
	public <T> void put(String key, T value, Duration ttl) {
		Instant expiry = (ttl != null && !ttl.isZero() && ttl.getSeconds() > 0)
				? Instant.now().plus(ttl)
				: null;
		cache.put(key, new CacheEntry(value, expiry));
	}

	@Override
	public boolean delete(String key) {
		return cache.remove(key) != null;
	}

	@Override
	public boolean exists(String key) {
		CacheEntry entry = cache.get(key);
		if (entry == null || entry.isExpired()) {
			cache.remove(key);
			return false;
		}
		return true;
	}

	@Override
	public boolean add(String setKey, String member) {
		return sets.computeIfAbsent(setKey, k -> ConcurrentHashMap.newKeySet()).add(member);
	}

	@Override
	public boolean remove(String setKey, String member) {
		Set<String> set = sets.get(setKey);
		return set != null && set.remove(member);
	}

	@Override
	public Set<String> get(String setKey) {
		Set<String> set = sets.get(setKey);
		return set != null ? new HashSet<>(set) : Set.of();
	}

	private void cleanup() {
		Instant now = Instant.now();
		cache.entrySet().removeIf(entry -> {
			CacheEntry ce = entry.getValue();
			return ce.expiry != null && ce.expiry.isBefore(now);
		});
	}

	private static class CacheEntry {
		final Object value;
		final Instant expiry;

		CacheEntry(Object value, Instant expiry) {
			this.value = value;
			this.expiry = expiry;
		}

		boolean isExpired() {
			return expiry != null && expiry.isBefore(Instant.now());
		}
	}
}
