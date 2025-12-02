package me.whereareiam.socialismus.adapter.module.provider.cache;

import com.google.inject.Singleton;
import me.whereareiam.socialismus.service.resource.CacheService;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

/**
 * A do-nothing implementation used when cache is off.
 */
@Singleton
public final class DummyCacheService implements CacheService {
	@Override
	public <T> Optional<T> get(String key, Class<T> type) {
		return Optional.empty();
	}

	@Override
	public <T> void put(String key, T value) {

	}

	@Override
	public <T> void put(String key, T value, Duration ttl) {

	}

	@Override
	public boolean delete(String key) {
		return false;
	}

	@Override
	public boolean exists(String key) {
		return false;
	}

	@Override
	public boolean add(String key, String member) {
		return false;
	}

	@Override
	public boolean remove(String key, String member) {
		return false;
	}

	@Override
	public Set<String> get(String key) {
		return Set.of();
	}
}
