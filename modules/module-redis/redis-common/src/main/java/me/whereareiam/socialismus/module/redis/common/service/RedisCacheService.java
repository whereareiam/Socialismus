package me.whereareiam.socialismus.module.redis.common.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import me.whereareiam.socialismus.service.resource.CacheService;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.io.*;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class RedisCacheService implements CacheService {
	private final JedisPool jedisPool;

	@Override
	public <T> Optional<T> get(String key, Class<T> type) {
		try (Jedis jedis = jedisPool.getResource()) {
			byte[] data = jedis.get(key.getBytes());
			if (data == null)
				return Optional.empty();

			try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data))) {
				Object obj = in.readObject();
				return Optional.of(type.cast(obj));
			} catch (IOException | ClassNotFoundException e) {
				throw new JedisException("Failed to deserialize cache value for key " + key, e);
			}
		}
	}

	@Override
	public <T> void put(String key, T value) {
		put(key, value, Duration.ZERO);
	}

	@Override
	public <T> void put(String key, T value, Duration ttl) {
		try (Jedis jedis = jedisPool.getResource()) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try (ObjectOutputStream oos = new ObjectOutputStream(bout)) {
				oos.writeObject(value);
			}
			byte[] data = bout.toByteArray();

			if (ttl != null && !ttl.isZero() && ttl.getSeconds() > 0) {
				jedis.setex(key.getBytes(), (int) ttl.getSeconds(), data);
				return;
			}

			jedis.set(key.getBytes(), data);
		} catch (IOException e) {
			throw new JedisException("Failed to serialize cache value for key " + key, e);
		}
	}

	@Override
	public boolean delete(String key) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.del(key) > 0;
		}
	}

	@Override
	public boolean exists(String key) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.exists(key);
		}
	}

	@Override
	public boolean add(String setKey, String member) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.sadd(setKey, member) > 0;
		}
	}

	@Override
	public boolean remove(String setKey, String member) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.srem(setKey, member) > 0;
		}
	}

	@Override
	public Set<String> get(String setKey) {
		try (Jedis jedis = jedisPool.getResource()) {
			return jedis.smembers(setKey);
		}
	}
}
