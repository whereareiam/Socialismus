package me.whereareiam.socialismus.module.redis.common.config.defaults;

import com.google.inject.Singleton;
import me.whereareiam.configura.merge.defaults.DefaultsProvider;
import me.whereareiam.socialismus.module.redis.api.model.RedisSettings;

@Singleton
public class RedisSettingsDefaults implements DefaultsProvider<RedisSettings> {
	@Override
	public RedisSettings supply(RedisSettings config) {
		RedisSettings.Redis redis = new RedisSettings.Redis();
		redis.setHost("localhost");
		redis.setPort(6379);
		redis.setPassword("");
		redis.setSsl(false);
		redis.setTimeout(2000);

		config.setRedis(redis);
		return config;
	}
}
