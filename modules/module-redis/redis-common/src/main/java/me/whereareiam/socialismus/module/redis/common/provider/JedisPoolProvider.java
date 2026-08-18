package me.whereareiam.socialismus.module.redis.common.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.Reloadable;
import me.whereareiam.socialismus.exception.ModuleLifecycleException;
import me.whereareiam.socialismus.module.redis.api.model.RedisSettings;
import me.whereareiam.socialismus.registry.base.Registry;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

@Singleton
public class JedisPoolProvider implements Provider<JedisPool>, Reloadable {
	private final Provider<RedisSettings> settings;
	private JedisPool jedisPool;

	@Inject
	public JedisPoolProvider(
			Provider<RedisSettings> settings,
			Registry<Reloadable> reloadableRegistry
	) {
		this.settings = settings;

		reloadableRegistry.register(this);
		get();
	}

	@Override
	public JedisPool get() {
		if (jedisPool != null)
			return jedisPool;

		RedisSettings.Redis redis = settings.get().getRedis();

		try {
			JedisPoolConfig poolConfig = new JedisPoolConfig();
			jedisPool = new JedisPool(
					poolConfig,
					redis.getHost(),
					redis.getPort(),
					(int) redis.getTimeout(),
					redis.isSsl()
			);

			// Test connection
			jedisPool.getResource().close();

			return jedisPool;
		} catch (JedisConnectionException e) {
			throw new ModuleLifecycleException("Failed to connect to Redis", e);
		}
	}

	@Override
	public void reload() {
		if (jedisPool != null)
			jedisPool.close();

		jedisPool = get();
	}
}
