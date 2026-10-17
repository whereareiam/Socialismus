package me.whereareiam.socialismus.module.redis.common.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import me.whereareiam.socialismus.event.EventListener;
import me.whereareiam.socialismus.event.EventManager;
import me.whereareiam.socialismus.event.base.SocialisticEvent;
import me.whereareiam.socialismus.event.plugin.PluginReloadedEvent;
import me.whereareiam.socialismus.exception.ModuleLifecycleException;
import me.whereareiam.socialismus.module.redis.api.model.RedisSettings;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

@Singleton
public class JedisPoolProvider implements Provider<JedisPool>, EventListener {
	private final Provider<RedisSettings> settings;
	private JedisPool jedisPool;

	@Inject
	public JedisPoolProvider(
			Provider<RedisSettings> settings,
			EventManager eventManager
	) {
		this.settings = settings;

		eventManager.register(this);
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

	@SocialisticEvent
	public void onReloaded(PluginReloadedEvent event) {
		if (jedisPool != null)
			jedisPool.close();

		jedisPool = get();
	}
}
