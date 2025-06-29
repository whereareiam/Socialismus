package me.whereareiam.socialismus.api.output.resource;

import java.util.Optional;

public interface CacheService {
	<T> Optional<T> get(String key, Class<T> type);

	void set(String key, Object value, long ttlSeconds);
}